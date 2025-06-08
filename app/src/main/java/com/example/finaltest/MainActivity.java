package com.example.finaltest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // 视图组件
    private GridView calendarGridView;
    private ListView scheduleListView;
    private BottomNavigationView bottomNavigationView;
    private TextView yearText, monthText, currentDateText;

    // 数据
    private Calendar currentCalendar;
    private final List<CalendarDay> calendarDays = new ArrayList<>();
    private final List<ScheduleItem> scheduleItems = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        initViews();

        // 初始化数据
        initData();

        // 设置监听器
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

        // 初始化日历
        initCalendarData();
        calendarGridView.setAdapter(new CalendarAdapter());

        // 初始化日程
        initScheduleData();
        scheduleListView.setAdapter(new ScheduleAdapter());

        // 更新标题显示
        updateCalendarTitle();
    }

    private void setupListeners() {
        // 年份切换
        findViewById(R.id.btn_prev_year).setOnClickListener(v -> {
            currentCalendar.add(Calendar.YEAR, -1);
            refreshCalendar();
        });

        findViewById(R.id.btn_next_year).setOnClickListener(v -> {
            currentCalendar.add(Calendar.YEAR, 1);
            refreshCalendar();
        });

        // 月份切换
        findViewById(R.id.btn_prev_month).setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            refreshCalendar();
        });

        findViewById(R.id.btn_next_month).setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            refreshCalendar();
        });

        // 日历项点击
        calendarGridView.setOnItemClickListener((parent, view, position, id) -> {
            CalendarDay day = calendarDays.get(position);
            if (day.isCurrentMonth) {
                showDateSelection(day);
                updateScheduleList(day.year, day.month, day.day);
            }
        });

        // 底部导航
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                // 已在主页
                return true;
            } else if (itemId == R.id.schedule) {
                Toast.makeText(this, "日程", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.course) {
                startActivity(new Intent(this, CourseScheduleActivity.class));
                return true;
            } else if (itemId == R.id.settings) {
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
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
            // 更新年份
            yearText.setText(new SimpleDateFormat("yyyy年", Locale.getDefault())
                    .format(currentCalendar.getTime()));

            // 更新月份
            monthText.setText(new SimpleDateFormat("M月", Locale.getDefault())
                    .format(currentCalendar.getTime()));

            // 更新当前日期
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

        // 添加上月日期
        Calendar prevMonth = (Calendar) calendar.clone();
        prevMonth.add(Calendar.MONTH, -1);
        int prevMonthMaxDay = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = prevMonthDays; i > 0; i--) {
            addCalendarDay(prevMonth, prevMonthMaxDay - i + 1, false);
        }

        // 添加本月日期
        int currentMonthMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar today = Calendar.getInstance();
        for (int i = 1; i <= currentMonthMaxDay; i++) {
            boolean isToday = isSameDay(calendar, today, i);
            addCalendarDay(calendar, i, true, isToday);
        }

        // 添加下月日期
        int remainingCells = 42 - (prevMonthDays + currentMonthMaxDay);
        for (int i = 1; i <= remainingCells; i++) {
            addCalendarDay(calendar, i, false);
        }
    }

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

    private boolean isSameDay(Calendar cal1, Calendar cal2, int day) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                day == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private void initScheduleData() {
        scheduleItems.clear();
        scheduleItems.add(new ScheduleItem("新活动", "16:14 - 21:00", false));
        scheduleItems.add(new ScheduleItem("会议", "全天", false));
        scheduleItems.add(new ScheduleItem("项目讨论", "全天", false));
        scheduleItems.add(new ScheduleItem("学习", "全天", false));
    }

    private void updateScheduleList(int year, int month, int day) {
        initScheduleData();
        ((BaseAdapter) scheduleListView.getAdapter()).notifyDataSetChanged();
    }

    // 数据类
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

        String getDateString() {
            return String.format(Locale.getDefault(), "%d-%02d-%02d", year, month, day);
        }
    }

    private static class ScheduleItem {
        final String title;
        final String time;
        boolean isChecked;

        ScheduleItem(String title, String time, boolean isChecked) {
            this.title = title;
            this.time = time;
            this.isChecked = isChecked;
        }
    }

    // 适配器类
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

    private class ScheduleAdapter extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = getOrCreateViewHolder(convertView, parent);
            bindScheduleItem(holder, position);
            return holder.rootView;
        }

        private ViewHolder getOrCreateViewHolder(View convertView, ViewGroup parent) {
            if (convertView == null) {
                View view = getLayoutInflater().inflate(R.layout.schedule_item, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.rootView = view;
                holder.titleTextView = view.findViewById(R.id.title_text);
                holder.timeTextView = view.findViewById(R.id.time_text);
                holder.checkBox = view.findViewById(R.id.checkbox);
                view.setTag(holder);
                return holder;
            }
            return (ViewHolder) convertView.getTag();
        }

        private void bindScheduleItem(ViewHolder holder, int position) {
            ScheduleItem item = scheduleItems.get(position);
            holder.titleTextView.setText(item.title);
            holder.timeTextView.setText(item.time);
            holder.checkBox.setChecked(item.isChecked);

            holder.checkBox.setOnClickListener(v ->
                    item.isChecked = !item.isChecked);

            holder.rootView.setOnLongClickListener(v -> {
                item.isChecked = !item.isChecked;
                holder.checkBox.setChecked(item.isChecked);
                return true;
            });
        }

        @Override public int getCount() { return scheduleItems.size(); }
        @Override public Object getItem(int position) { return scheduleItems.get(position); }
        @Override public long getItemId(int position) { return position; }

        private class ViewHolder {
            View rootView;
            TextView titleTextView;
            TextView timeTextView;
            CheckBox checkBox;
        }
    }
}