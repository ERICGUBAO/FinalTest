package com.example.finaltest;

public class CourseItem {
    public String courseName;
    public String teacher;
    public String classroom;
    public String time;
    public int color;

    public CourseItem(String courseName, String teacher, String classroom, String time, int color) {
        this.courseName = courseName;
        this.teacher = teacher;
        this.classroom = classroom;
        this.time = time;
        this.color = color;
    }


    // 可选：添加toString方法便于调试
    @Override
    public String toString() {
        return "CourseItem{" +
                "courseName='" + courseName + '\'' +
                ", teacher='" + teacher + '\'' +
                ", classroom='" + classroom + '\'' +
                ", time='" + time + '\'' +
                ", color=" + color +
                '}';
    }
}