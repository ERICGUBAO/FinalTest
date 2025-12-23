42312153 张顾宝
做的是一个日历（日程表）+课程表的一个混合项目

## AES 文本加密脚本（Python）

仓库根目录下新增了 `aes_text_crypto.py`，使用 AES-128-CBC 对 UTF-8 文本文件进行加解密。依赖 PyCryptodome，可用以下命令安装：

```bash
pip install -r requirements.txt
```

示例用法：

```bash
# 加密
python aes_text_crypto.py encrypt -i plain.txt -o cipher.bin -k "mysecretkey123"

# 解密
python aes_text_crypto.py decrypt -i cipher.bin -o decrypted.txt -k "mysecretkey123"
```

> 说明：按照课程报告要求，密钥规范化采用简单的截断/零填充方式，便于演示但不适合生产环境。实际应用请使用 PBKDF2、scrypt 等口令派生函数结合随机盐来生成密钥。
