package com.example.finaltest;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class CourseDataManager {
    private static final String PREFS_NAME = "CourseSchedulePrefs";
    private static final String COURSE_LIST_KEY = "course_list";

    public static void saveCourses(Context context, List<CourseItem> courses) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // 将课程列表转换为JSON字符串数组
        List<String> jsonList = new ArrayList<>();
        for (CourseItem course : courses) {
            jsonList.add(course.toJson());
        }

        // 保存为字符串集合
        editor.putStringSet(COURSE_LIST_KEY, new java.util.HashSet<>(jsonList));
        editor.apply();
    }

    public static List<CourseItem> loadCourses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        java.util.Set<String> jsonSet = prefs.getStringSet(COURSE_LIST_KEY, new java.util.HashSet<>());

        List<CourseItem> courses = new ArrayList<>();
        for (String jsonStr : jsonSet) {
            CourseItem course = CourseItem.fromJson(jsonStr);
            if (course != null) {
                courses.add(course);
            }
        }
        return courses;
    }

    public static void clearAllData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}