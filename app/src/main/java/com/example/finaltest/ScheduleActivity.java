package com.example.finaltest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    private ListView scheduleListView;
    private FloatingActionButton fabAddSchedule;
    private List<String> scheduleItems = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        scheduleListView = findViewById(R.id.schedule_list);
        fabAddSchedule = findViewById(R.id.fab_add_schedule);

        // 初始化日程数据
        initScheduleData();

        // 设置适配器
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleItems);
        scheduleListView.setAdapter(adapter);

        // 添加日程按钮点击事件
        fabAddSchedule.setOnClickListener(v -> showAddScheduleDialog());

        // 长按删除日程
        scheduleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteScheduleDialog(position);
                return true;
            }
        });
    }

    private void initScheduleData() {
        scheduleItems.add("新活动");
        scheduleItems.add("会议");
        scheduleItems.add("项目讨论");
        scheduleItems.add("学习");
    }

    private void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加日程");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String scheduleTitle = input.getText().toString();
                if (!scheduleTitle.isEmpty()) {
                    scheduleItems.add(scheduleTitle);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(ScheduleActivity.this, "日程添加成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScheduleActivity.this, "日程标题不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showDeleteScheduleDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除日程");
        builder.setMessage("确定要删除该日程吗？");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scheduleItems.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(ScheduleActivity.this, "日程删除成功", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}