package com.example.finaltest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    private ScheduleAdapter adapter;
    private final List<ScheduleItem> scheduleItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        ListView scheduleListView = findViewById(R.id.schedule_list);
        Button btnAddSchedule = findViewById(R.id.fab_add_schedule);

        initScheduleData();
        adapter = new ScheduleAdapter(this, R.layout.schedule_item, scheduleItems);
        scheduleListView.setAdapter(adapter);

        btnAddSchedule.setOnClickListener(v -> showAddScheduleDialog());

        scheduleListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteScheduleDialog(position);
            return true;
        });
    }

    // 其余代码保持不变...
    private void initScheduleData() {
        scheduleItems.add(new ScheduleItem("团队会议", "09:00 - 10:30", false));
        scheduleItems.add(new ScheduleItem("午餐时间", "12:00 - 13:00", false));
        scheduleItems.add(new ScheduleItem("客户访谈", "14:00 - 15:30", false));
    }

    private void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加日程");

        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_schedule, null);
        builder.setView(dialogView);

        final EditText titleInput = dialogView.findViewById(R.id.edit_schedule_title);
        final EditText timeInput = dialogView.findViewById(R.id.edit_schedule_time);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String title = titleInput.getText().toString();
            String time = timeInput.getText().toString();

            if (!title.isEmpty()) {
                if (time.isEmpty()) time = "时间待定";

                scheduleItems.add(new ScheduleItem(title, time, false));
                adapter.notifyDataSetChanged();
                Toast.makeText(ScheduleActivity.this, "日程添加成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ScheduleActivity.this, "日程标题不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showDeleteScheduleDialog(final int position) {
        if (position < 0 || position >= scheduleItems.size()) {
            Toast.makeText(this, "无效的日程项", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("删除日程")
                .setMessage("确定要删除 '" + scheduleItems.get(position).title + "' 吗？")
                .setPositiveButton("确定", (dialog, which) -> {
                    scheduleItems.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "日程已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private static class ScheduleAdapter extends ArrayAdapter<ScheduleItem> {
        public ScheduleAdapter(Context context, int resource, List<ScheduleItem> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ScheduleItem item = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.schedule_item, parent, false);
            }

            TextView titleText = convertView.findViewById(R.id.title_text);
            TextView timeText = convertView.findViewById(R.id.time_text);

            titleText.setText(item.title);
            timeText.setText(item.time);

            return convertView;
        }
    }

    private static class ScheduleItem {
        String title;
        String time;
        boolean completed;

        ScheduleItem(String title, String time, boolean completed) {
            this.title = title;
            this.time = time;
            this.completed = completed;
        }
    }
}