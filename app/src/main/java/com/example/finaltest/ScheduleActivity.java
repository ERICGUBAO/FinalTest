package com.example.finaltest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private ScheduleAdapter adapter;
    private List<ScheduleItem> scheduleItems = new ArrayList<>();
    private Calendar selectedDate = Calendar.getInstance();
    private static final String PREFS_NAME = "SchedulePrefs";
    private static final String SCHEDULE_LIST_KEY = "scheduleList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        ListView scheduleListView = findViewById(R.id.schedule_list);
        Button btnAddSchedule = findViewById(R.id.fab_add_schedule);

        loadScheduleData();

        adapter = new ScheduleAdapter(this, R.layout.schedule_item, scheduleItems);
        scheduleListView.setAdapter(adapter);

        btnAddSchedule.setOnClickListener(v -> showAddScheduleDialog());

        scheduleListView.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteScheduleDialog(position);
            return true;
        });
    }

    private void loadScheduleData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String jsonString = prefs.getString(SCHEDULE_LIST_KEY, null);

        if (jsonString != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonString);
                scheduleItems.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ScheduleItem item = new ScheduleItem(
                            jsonObject.getString("title"),
                            jsonObject.getString("time"),
                            jsonObject.getString("date"),
                            jsonObject.getBoolean("isChecked")
                    );
                    scheduleItems.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "加载日程数据失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            initScheduleData();
        }
    }

    private void saveScheduleData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        try {
            JSONArray jsonArray = new JSONArray();
            for (ScheduleItem item : scheduleItems) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", item.title);
                jsonObject.put("time", item.time);
                jsonObject.put("date", item.date);
                jsonObject.put("isChecked", item.isChecked);
                jsonArray.put(jsonObject);
            }
            editor.putString(SCHEDULE_LIST_KEY, jsonArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "保存日程数据失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initScheduleData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(Calendar.getInstance().getTime());

        scheduleItems.add(new ScheduleItem("团队会议", "09:00 - 10:30", today, false));
        scheduleItems.add(new ScheduleItem("午餐时间", "12:00 - 13:00", today, false));
        scheduleItems.add(new ScheduleItem("客户访谈", "14:00 - 15:30", today, false));

        saveScheduleData();
    }

    private void showAddScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加日程");

        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_schedule, null);
        builder.setView(dialogView);

        final EditText titleInput = dialogView.findViewById(R.id.edit_schedule_title);
        final EditText timeInput = dialogView.findViewById(R.id.edit_schedule_time);
        final TextView dateText = dialogView.findViewById(R.id.date_text);
        final Button btnSelectDate = dialogView.findViewById(R.id.btn_select_date);

        updateDateText(dateText);

        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(
                    ScheduleActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        updateDateText(dateText);
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        builder.setPositiveButton("确定", (dialog, which) -> {
            String title = titleInput.getText().toString();
            String time = timeInput.getText().toString();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getTime());

            if (!title.isEmpty()) {
                if (time.isEmpty()) time = "时间待定";

                scheduleItems.add(new ScheduleItem(title, time, date, false));
                adapter.notifyDataSetChanged();
                saveScheduleData();
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
                    saveScheduleData();
                    Toast.makeText(this, "日程已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateDateText(TextView dateText) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        dateText.setText(sdf.format(selectedDate.getTime()));
    }

    private static class ScheduleAdapter extends ArrayAdapter<ScheduleItem> {
        public ScheduleAdapter(Context context, int resource, List<ScheduleItem> items) {
            super(context, resource, items);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ScheduleItem item = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.schedule_item, parent, false);
            }

            TextView titleText = convertView.findViewById(R.id.title_text);
            TextView timeText = convertView.findViewById(R.id.time_text);
            TextView dateText = convertView.findViewById(R.id.date_text);

            titleText.setText(item.title);
            timeText.setText(item.time);
            dateText.setText(item.date);

            return convertView;
        }
    }
}