package com.example.finaltest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchTheme;
    private Button btnClearCache;
    private SharedPreferences sharedPreferences;
    private SharedPreferences coursePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchTheme = findViewById(R.id.switch_theme);
        btnClearCache = findViewById(R.id.btn_clear_cache);

        // 主题设置用的SharedPreferences
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        // 课程数据用的SharedPreferences
        coursePreferences = getSharedPreferences("CourseSchedulePrefs", MODE_PRIVATE);

        // 初始化主题开关状态
        boolean isDarkTheme = sharedPreferences.getBoolean("dark_theme", false);
        switchTheme.setChecked(isDarkTheme);
        setThemeMode(isDarkTheme);

        // 主题切换开关点击事件
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setThemeMode(isChecked);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("dark_theme", isChecked);
            editor.apply();
        });

        // 清除缓存按钮点击事件 - 修改为清除课程数据
        btnClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCourseData();
                clearAppCache();
                Toast.makeText(SettingsActivity.this, "所有课程数据已清除", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setThemeMode(boolean isDarkTheme) {
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // 清除课程数据
    private void clearCourseData() {
        SharedPreferences.Editor editor = coursePreferences.edit();
        editor.clear(); // 清除所有课程数据
        editor.apply();
    }

    // 清除应用缓存（可选）
    private void clearAppCache() {
        try {
            File cacheDir = getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                for (File child : cacheDir.listFiles()) {
                    child.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}