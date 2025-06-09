package com.example.finaltest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private GridView calendarGridView;
    private ListView scheduleListView;
    private BottomNavigationView bottomNavigationView;
    private TextView yearText, monthText, currentDateText;
    private Calendar currentCalendar;
    private final List<CalendarDay> calendarDays = new ArrayList<>();
    private final List<ScheduleItem> scheduleItems = new ArrayList<>();
    private static final String PREFS_NAME = "SchedulePrefs";
    private static final String SCHEDULE_LIST_KEY = "scheduleList";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initData();
        setupListeners();
    }

    private void initViews() {
        calendarGridView = findViewById(R.id.calendar_grid);
        scheduleListView = findViewById(R.id.schedule_list);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        yearText = findViewById(R.id.year_text);
        monthText = findViewById(R.id.month_text);
        currentDateText = findViewById(R.id.current_date);
    }

    private void initData() {
        currentCalendar = Calendar.getInstance();
        initCalendarData();
        calendarGridView.setAdapter(new CalendarAdapter());
        loadScheduleData();
        updateScheduleList(currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH) + 1,
                currentCalendar.get(Calendar.DAY_OF_MONTH));
        updateCalendarTitle();
    }

    private void loadScheduleData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String jsonString = prefs.getString(SCHEDULE_LIST_KEY, null);

        if (jsonString == null) {
            // 初始化一些示例数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = sdf.format(Calendar.getInstance().getTime());
            scheduleItems.add(new ScheduleItem("示例会议", "09:00 - 10:30", today, false));
            saveScheduleData();
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
        }
    }

    private void setupListeners() {
        findViewById(R.id.btn_prev_year).setOnClickListener(v -> {
            currentCalendar.add(Calendar.YEAR, -1);
            refreshCalendar();
        });

        findViewById(R.id.btn_next_year).setOnClickListener(v -> {
            currentCalendar.add(Calendar.YEAR, 1);
            refreshCalendar();
        });

        findViewById(R.id.btn_prev_month).setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            refreshCalendar();
        });

        findViewById(R.id.btn_next_month).setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            refreshCalendar();
        });

        calendarGridView.setOnItemClickListener((parent, view, position, id) -> {
            CalendarDay day = calendarDays.get(position);
            if (day.isCurrentMonth) {
                showDateSelection(day);
                updateScheduleList(day.year, day.month, day.day);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                return true;
            } else if (itemId == R.id.schedule) {
                startActivity(new Intent(this, ScheduleActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            } else if (itemId == R.id.course) {
                startActivity(new Intent(this, CourseScheduleActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            } else if (itemId == R.id.settings) {
                startActivity(new Intent(this, SettingsActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            }
            return false;
        });
    }

    private void refreshCalendar() {
        initCalendarData();
        ((BaseAdapter) calendarGridView.getAdapter()).notifyDataSetChanged();
        updateCalendarTitle();
    }

    private void updateCalendarTitle() {
        try {
            yearText.setText(new SimpleDateFormat("yyyy年", Locale.getDefault())
                    .format(currentCalendar.getTime()));
            monthText.setText(new SimpleDateFormat("M月", Locale.getDefault())
                    .format(currentCalendar.getTime()));
            currentDateText.setText(new SimpleDateFormat("M月d日 E", Locale.getDefault())
                    .format(Calendar.getInstance().getTime()));
        } catch (Exception e) {
            Log.e("MainActivity", "日期格式化错误", e);
        }
    }

    private void showDateSelection(CalendarDay day) {
        Toast.makeText(this,
                String.format(Locale.getDefault(),
                        "选择日期: %d-%02d-%02d",
                        day.year, day.month, day.day),
                Toast.LENGTH_SHORT).show();
    }

    private void initCalendarData() {
        calendarDays.clear();
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int prevMonthDays = firstDayOfWeek - Calendar.SUNDAY;

        Calendar prevMonth = (Calendar) calendar.clone();
        prevMonth.add(Calendar.MONTH, -1);
        int prevMonthMaxDay = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = prevMonthDays; i > 0; i--) {
            addCalendarDay(prevMonth, prevMonthMaxDay - i + 1, false);
        }

        int currentMonthMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar today = Calendar.getInstance();
        for (int i = 1; i <= currentMonthMaxDay; i++) {
            boolean isToday = isSameDay(calendar, today, i);
            addCalendarDay(calendar, i, true, isToday);
        }

        int remainingCells = 42 - (prevMonthDays + currentMonthMaxDay);
        for (int i = 1; i <= remainingCells; i++) {
            addCalendarDay(calendar, i, false);
        }
    }

    private void updateScheduleList(int year, int month, int day) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String jsonString = prefs.getString(SCHEDULE_LIST_KEY, null);
        List<ScheduleItem> filteredItems = new ArrayList<>();

        if (jsonString != null) {
            try {
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
                JSONArray jsonArray = new JSONArray(jsonString);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("date").equals(selectedDate)) {
                        filteredItems.add(new ScheduleItem(
                                jsonObject.getString("title"),
                                jsonObject.getString("time"),
                                jsonObject.getString("date"),
                                jsonObject.getBoolean("isChecked")
                        ));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        scheduleListView.setAdapter(new MainScheduleAdapter(filteredItems));
    }

    // ... 保持其他辅助方法不变 ...

    private class MainScheduleAdapter extends BaseAdapter {
        private final List<ScheduleItem> items;

        MainScheduleAdapter(List<ScheduleItem> items) {
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.schedule_item, parent, false);
            }

            ScheduleItem item = items.get(position);
            TextView titleText = convertView.findViewById(R.id.title_text);
            TextView timeText = convertView.findViewById(R.id.time_text);
            TextView dateText = convertView.findViewById(R.id.date_text);

            titleText.setText(item.title);
            timeText.setText(item.time);
            dateText.setText(formatDate(item.date));

            return convertView;
        }

        private String formatDate(String dateStr) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
                return outputFormat.format(inputFormat.parse(dateStr));
            } catch (Exception e) {
                return dateStr;
            }
        }

        @Override public int getCount() { return items.size(); }
        @Override public Object getItem(int position) { return items.get(position); }
        @Override public long getItemId(int position) { return position; }
    }

    // 新增：CalendarDay类（来自第二段代码）
    private static class CalendarDay {
        final int year;
        final int month;
        final int day;
        final boolean isCurrentMonth;
        final boolean isToday;

        CalendarDay(int year, int month, int day, boolean isCurrentMonth, boolean isToday) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.isCurrentMonth = isCurrentMonth;
            this.isToday = isToday;
        }
    }

    // 新增：CalendarAdapter类（来自第二段代码）
    private class CalendarAdapter extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(MainActivity.this);
                int cellSize = getResources().getDimensionPixelSize(R.dimen.calendar_cell_size);
                textView.setLayoutParams(new GridView.LayoutParams(cellSize, cellSize));
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            } else {
                textView = (TextView) convertView;
            }

            CalendarDay day = calendarDays.get(position);
            textView.setText(String.valueOf(day.day));

            if (day.isToday) {
                textView.setBackgroundResource(R.drawable.bg_today);
                textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.today_text));
            } else if (day.isCurrentMonth) {
                textView.setBackgroundColor(Color.TRANSPARENT);
                textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.current_month_text));
            } else {
                textView.setBackgroundColor(Color.TRANSPARENT);
                textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.other_month_text));
            }

            return textView;
        }

        @Override public int getCount() { return calendarDays.size(); }
        @Override public Object getItem(int position) { return calendarDays.get(position); }
        @Override public long getItemId(int position) { return position; }
    }

    // 新增：addCalendarDay方法（来自第二段代码）
    private void addCalendarDay(Calendar calendar, int day, boolean isCurrentMonth) {
        addCalendarDay(calendar, day, isCurrentMonth, false);
    }

    private void addCalendarDay(Calendar calendar, int day, boolean isCurrentMonth, boolean isToday) {
        calendarDays.add(new CalendarDay(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                day,
                isCurrentMonth,
                isToday
        ));
    }

    // 新增：isSameDay方法（来自第二段代码）
    private boolean isSameDay(Calendar cal1, Calendar cal2, int day) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                day == cal2.get(Calendar.DAY_OF_MONTH);
    }
}