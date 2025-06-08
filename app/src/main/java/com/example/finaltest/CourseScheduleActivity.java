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
    private static final int DAYS_IN_WEEK = 7;
    private static final int PERIODS_PER_DAY = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_schedule);

        recyclerView = findViewById(R.id.course_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 8);
        recyclerView.setLayoutManager(layoutManager);

        // 从保存的状态恢复课程（实际应用中应使用数据库）
        if (savedInstanceState != null) {
            courseItems = savedInstanceState.getParcelableArrayList("courseItems");
        }

        if (courseItems == null || courseItems.isEmpty()) {
            initCourseData();
        }

        sortCourseItems();
        adapter = new CourseScheduleAdapter(courseItems, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_course);
        fab.setOnClickListener(v -> showAddCourseDialog());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("courseItems", new ArrayList<>(courseItems));
    }

    private void initCourseData() {

    }

    private void showAddCourseDialog() {
        AddCourseDialog dialog = new AddCourseDialog(this, newCourse -> {
            courseItems.add(newCourse);
            sortCourseItems();
            adapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    public void sortCourseItems() {
        Collections.sort(courseItems, (o1, o2) ->
                parseTimeToOrder(o1.time) - parseTimeToOrder(o2.time));
    }

    private int parseTimeToOrder(String time) {
        String[] parts = time.split("\\s+");
        if (parts.length < 2) return 0;
        String day = parts[0];
        String periodStr = parts[1].replaceAll("[^0-9:-]", "");
        int dayOrder = getDayOrder(day);
        if (dayOrder == -1) return 0;
        int startHour = Integer.parseInt(periodStr.split("-")[0].split(":")[0]);
        return dayOrder * PERIODS_PER_DAY + (startHour - 8);
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