package com.example.finaltest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CourseScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseScheduleAdapter adapter;
    private List<CourseItem> courseItems = new ArrayList<>();
    private static final int DAYS_IN_WEEK = 7; // 一周7天
    private static final int PERIODS_PER_DAY = 12; // 每天12节课

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_schedule);

        recyclerView = findViewById(R.id.course_recycler_view);
        // 使用GridLayoutManager实现7列网格（时间列+7天）
        GridLayoutManager layoutManager = new GridLayoutManager(this, 8);
        recyclerView.setLayoutManager(layoutManager);

        initCourseData();
        sortCourseItems();
        adapter = new CourseScheduleAdapter(courseItems, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_course);
        fab.setOnClickListener(v -> showAddCourseDialog());
    }

    private void initCourseData() {
        courseItems.add(new CourseItem("数学", "张老师", "A101", "周一 8:00-9:30", 0xFF4285F4));
        courseItems.add(new CourseItem("英语", "李老师", "B205", "周二 10:00-11:30", 0xFFEA4335));
        courseItems.add(new CourseItem("物理", "王老师", "C302", "周三 13:00-14:30", 0xFFFBBC05));
    }

    private void showAddCourseDialog() {
        // 实现添加课程对话框（需自行补充）
    }

    public void sortCourseItems() {
        Collections.sort(courseItems, new Comparator<CourseItem>() {
            @Override
            public int compare(CourseItem o1, CourseItem o2) {
                return parseTimeToOrder(o1.time) - parseTimeToOrder(o2.time);
            }
        });
    }

    // 将时间解析为排序序号（例如：周一第1节=1，周二第3节=10）
    private int parseTimeToOrder(String time) {
        String[] parts = time.split("\\s+");
        if (parts.length < 2) return 0;
        String day = parts[0];
        String periodStr = parts[1].replaceAll("[^0-9:-]", ""); // 提取数字和符号
        int dayOrder = getDayOrder(day);
        if (dayOrder == -1) return 0;
        // 解析时间段（如8:00-9:30转换为起始节数，假设每节课90分钟）
        int startHour = Integer.parseInt(periodStr.split("-")[0].split(":")[0]);
        return dayOrder * PERIODS_PER_DAY + (startHour - 8); // 假设8点开始第1节课
    }

    private int getDayOrder(String day) {
        switch (day) {
            case "周一": return 1;
            case "周二": return 2;
            case "周三": return 3;
            case "周四": return 4;
            case "周五": return 5;
            case "周六": return 6;
            case "周日": return 7;
            default: return -1;
        }
    }

    public void showDeleteCourseDialog(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("删除课程")
                .setMessage("确定要删除该课程吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    courseItems.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

}