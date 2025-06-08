package com.example.finaltest;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class CourseScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseScheduleAdapter adapter;
    private List<CourseItem> courseItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_schedule);

        recyclerView = findViewById(R.id.course_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化测试数据
        initCourseData();

        adapter = new CourseScheduleAdapter(courseItems);
        recyclerView.setAdapter(adapter);

        // 添加课程按钮
        FloatingActionButton fab = findViewById(R.id.fab_add_course);
        fab.setOnClickListener(v -> showAddCourseDialog());
    }

    private void initCourseData() {
        courseItems.add(new CourseItem("数学", "张老师", "A101", "周一 8:00-9:30", 0xFF4285F4));
        courseItems.add(new CourseItem("英语", "李老师", "B205", "周二 10:00-11:30", 0xFFEA4335));
        courseItems.add(new CourseItem("物理", "王老师", "C302", "周三 13:00-14:30", 0xFFFBBC05));
    }

    private void showAddCourseDialog() {
        // 实现添加课程对话框
        AddCourseDialog dialog = new AddCourseDialog(this, (name, teacher, room, time, color) -> {
            courseItems.add(new CourseItem(name, teacher, room, time, color));
            adapter.notifyDataSetChanged();
        });
        dialog.show();
    }
}