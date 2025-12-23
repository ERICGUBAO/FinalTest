#!/usr/bin/env python3
"""AES-128-CBC text file encryption and decryption utility."""

from __future__ import annotations

import argparse
from pathlib import Path

from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes

BLOCK_SIZE = 16


def pkcs7_pad(data: bytes) -> bytes:
    """Apply PKCS#7 padding."""
    padding_len = BLOCK_SIZE - (len(data) % BLOCK_SIZE)
    return data + bytes([padding_len]) * padding_len


def pkcs7_unpad(data: bytes) -> bytes:
    """Remove PKCS#7 padding, raising on invalid padding."""
    if not data:
        raise ValueError("Cannot unpad empty data.")

    padding_len = data[-1]
    if not 1 <= padding_len <= BLOCK_SIZE:
        raise ValueError("Invalid padding length.")

    if data[-padding_len:] != bytes([padding_len]) * padding_len:
        raise ValueError("Invalid PKCS#7 padding.")

    return data[:-padding_len]


def normalize_key(key_str: str) -> bytes:
    """Normalize arbitrary length key string to 16 bytes using UTF-8.

    This mirrors the simple truncation/zero-padding approach described in the
    accompanying write-up; for production deployments prefer a KDF such as
    PBKDF2 or scrypt.
    """
    key_bytes = key_str.encode("utf-8")
    if len(key_bytes) < BLOCK_SIZE:
        key_bytes = key_bytes + b"\x00" * (BLOCK_SIZE - len(key_bytes))
    return key_bytes[:BLOCK_SIZE]


def encrypt_file(input_path: str, output_path: str, key_str: str) -> None:
    """Encrypt UTF-8 text file and write IV + ciphertext."""
    plaintext = Path(input_path).read_text(encoding="utf-8").encode("utf-8")
    key = normalize_key(key_str)
    iv = get_random_bytes(BLOCK_SIZE)
    cipher = AES.new(key, AES.MODE_CBC, iv)
    ciphertext = cipher.encrypt(pkcs7_pad(plaintext))

    with open(output_path, "wb") as out_file:
        out_file.write(iv + ciphertext)


def decrypt_file(input_path: str, output_path: str, key_str: str) -> None:
    """Decrypt file created by encrypt_file and restore UTF-8 text."""
    ciphertext = Path(input_path).read_bytes()
    if len(ciphertext) < BLOCK_SIZE:
        raise ValueError("Ciphertext is too short to contain an IV.")

    key = normalize_key(key_str)
    iv, payload = ciphertext[:BLOCK_SIZE], ciphertext[BLOCK_SIZE:]
    if len(payload) % BLOCK_SIZE != 0:
        raise ValueError("Ciphertext payload is not a multiple of the block size.")
    cipher = AES.new(key, AES.MODE_CBC, iv)
    padded_plaintext = cipher.decrypt(payload)
    plaintext = pkcs7_unpad(padded_plaintext).decode("utf-8")

    Path(output_path).write_text(plaintext, encoding="utf-8")


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="AES-128-CBC utility for encrypting/decrypting UTF-8 text files."
    )
    subparsers = parser.add_subparsers(dest="command", required=True)

    encrypt_parser = subparsers.add_parser("encrypt", help="Encrypt a UTF-8 text file.")
    encrypt_parser.add_argument("-i", "--input", required=True, help="Path to plaintext UTF-8 text file.")
    encrypt_parser.add_argument("-o", "--output", required=True, help="Path to write binary ciphertext.")
    encrypt_parser.add_argument("-k", "--key", required=True, help="Key string (will be normalized to 16 bytes).")

    decrypt_parser = subparsers.add_parser("decrypt", help="Decrypt a ciphertext file produced by this tool.")
    decrypt_parser.add_argument("-i", "--input", required=True, help="Path to binary ciphertext (IV||C).")
    decrypt_parser.add_argument("-o", "--output", required=True, help="Path to write recovered UTF-8 text.")
    decrypt_parser.add_argument("-k", "--key", required=True, help="Key string (must match encryption key).")

    return parser


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()

    try:
        if args.command == "encrypt":
            encrypt_file(args.input, args.output, args.key)
        elif args.command == "decrypt":
            decrypt_file(args.input, args.output, args.key)
    except ValueError as exc:
        parser.exit(status=1, message=f"Operation failed: {exc}\n")
    except OSError:
        parser.exit(
            status=1, message=f"Operation failed: file read/write error during {args.command}.\n"
        )
    except UnicodeError:
        parser.exit(status=1, message="Operation failed: encoding or decoding error.\n")


if __name__ == "__main__":
    main()
