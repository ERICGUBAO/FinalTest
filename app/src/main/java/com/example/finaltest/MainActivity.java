package com.example.finaltest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.widget.CheckBox;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private GridView calendarGridView;
    private ListView scheduleListView;
    private BottomNavigationView bottomNavigationView;
    private Calendar currentCalendar;
    private List<CalendarDay> calendarDays = new ArrayList<>();
    private List<ScheduleItem> scheduleItems = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        tabLayout = findViewById(R.id.tab_layout);
        calendarGridView = findViewById(R.id.calendar_grid);
        scheduleListView = findViewById(R.id.schedule_list);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        TextView currentDateTextView = findViewById(R.id.current_date);
        TextView lunarDateTextView = findViewById(R.id.lunar_date);

        // 设置当前日期和农历（示例）
        currentCalendar = Calendar.getInstance();
        int month = currentCalendar.get(Calendar.MONTH) + 1;
        int day = currentCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateTextView.setText(month + "月" + day + "日");
        lunarDateTextView.setText("农历: 二月初二"); // 示例，实际应使用农历算法

        // 初始化日历数据
        initCalendarData();

        // 设置日历适配器
        CalendarAdapter calendarAdapter = new CalendarAdapter();
        calendarGridView.setAdapter(calendarAdapter);

        // 设置日历项点击事件
        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CalendarDay day = calendarDays.get(position);
                if (day.isCurrentMonth) {
                    Toast.makeText(MainActivity.this, "选择日期: " + day.year + "-" + day.month + "-" + day.day, Toast.LENGTH_SHORT).show();
                    // 更新日程列表
                    updateScheduleList(day.year, day.month, day.day);
                }
            }
        });

        // 初始化日程数据
        initScheduleData();

        // 设置日程适配器
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter();
        scheduleListView.setAdapter(scheduleAdapter);

        // 设置底部导航栏事件
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                Toast.makeText(this, "主页", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.schedule) {
                Toast.makeText(this, "日程", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.course) {
                Toast.makeText(this, "课程表", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.settings) {
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    // 初始化日历数据
    private void initCalendarData() {
        calendarDays.clear();

        // 获取当月第一天是星期几
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 获取当月总天数
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 计算上个月需要显示的天数
        int prevMonthDays = firstDayOfWeek - Calendar.SUNDAY;

        // 获取上个月的最后几天
        Calendar prevCalendar = (Calendar) calendar.clone();
        prevCalendar.add(Calendar.MONTH, -1);
        int prevMonthLastDay = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // 添加上个月的最后几天
        for (int i = prevMonthDays; i > 0; i--) {
            calendarDays.add(new CalendarDay(
                    prevCalendar.get(Calendar.YEAR),
                    prevCalendar.get(Calendar.MONTH) + 1,
                    prevMonthLastDay - i + 1,
                    false,
                    false
            ));
        }

        // 添加当月的天数
        for (int i = 1; i <= daysInMonth; i++) {
            boolean isToday = (i == currentCalendar.get(Calendar.DAY_OF_MONTH) &&
                    calendar.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH) &&
                    calendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR));

            calendarDays.add(new CalendarDay(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    i,
                    true,
                    isToday
            ));
        }

        // 添加下个月的开始几天，使网格填满
        int nextMonthDays = 42 - (prevMonthDays + daysInMonth);
        Calendar nextCalendar = (Calendar) calendar.clone();
        nextCalendar.add(Calendar.MONTH, 1);

        for (int i = 1; i <= nextMonthDays; i++) {
            calendarDays.add(new CalendarDay(
                    nextCalendar.get(Calendar.YEAR),
                    nextCalendar.get(Calendar.MONTH) + 1,
                    i,
                    false,
                    false
            ));
        }
    }

    // 初始化日程数据
    private void initScheduleData() {
        scheduleItems.clear();
        scheduleItems.add(new ScheduleItem("新活动", "16:14 - 21:00", false));
        scheduleItems.add(new ScheduleItem("会议", "全天", false));
        scheduleItems.add(new ScheduleItem("项目讨论", "全天", false));
        scheduleItems.add(new ScheduleItem("学习", "全天", false));
    }

    // 更新日程列表
    private void updateScheduleList(int year, int month, int day) {
        // 根据选择的日期更新日程列表
        // 这里只是示例，实际应用中应该从数据库或其他数据源获取数据
        initScheduleData();
        ScheduleAdapter adapter = (ScheduleAdapter) scheduleListView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    // 日历项数据类
    private static class CalendarDay {
        int year;
        int month;
        int day;
        boolean isCurrentMonth;
        boolean isToday;

        public CalendarDay(int year, int month, int day, boolean isCurrentMonth, boolean isToday) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.isCurrentMonth = isCurrentMonth;
            this.isToday = isToday;
        }
    }

    // 日程项数据类
    private static class ScheduleItem {
        String title;
        String time;
        boolean isChecked;

        public ScheduleItem(String title, String time, boolean isChecked) {
            this.title = title;
            this.time = time;
            this.isChecked = isChecked;
        }
    }

    // 日历适配器
    private class CalendarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return calendarDays.size();
        }

        @Override
        public Object getItem(int position) {
            return calendarDays.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = new TextView(MainActivity.this);
                textView.setLayoutParams(new GridView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        120
                ));
                textView.setPadding(8, 8, 8, 8);
                textView.setTextSize(16);
                textView.setGravity(android.view.Gravity.CENTER);
            } else {
                textView = (TextView) convertView;
            }

            CalendarDay day = calendarDays.get(position);
            textView.setText(String.valueOf(day.day));

            if (day.isToday) {
                // 当前日期使用特殊背景和文字颜色
                textView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.today_background));
                textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.today_text));
            } else if (day.isCurrentMonth) {
                // 当前月的日期使用默认样式
                textView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.current_month_background));
                textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.current_month_text));
            } else {
                // 不是当前月的日期使用淡色
                textView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.other_month_background));
                textView.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.other_month_text));
            }

            return textView;
        }
    }

    // 日程适配器
    private class ScheduleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return scheduleItems.size();
        }

        @Override
        public Object getItem(int position) {
            return scheduleItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.schedule_item, parent, false);
                holder = new ViewHolder();
                holder.titleTextView = convertView.findViewById(R.id.title_text);
                holder.timeTextView = convertView.findViewById(R.id.time_text);
                holder.checkBox = convertView.findViewById(R.id.checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ScheduleItem item = scheduleItems.get(position);
            holder.titleTextView.setText(item.title);
            holder.timeTextView.setText(item.time);
            holder.checkBox.setChecked(item.isChecked);

            // 设置复选框点击事件
            holder.checkBox.setOnClickListener(v -> {
                item.isChecked = !item.isChecked;
                // 可以在这里添加保存勾选状态的逻辑
            });

            // 设置长按事件
            convertView.setOnLongClickListener(v -> {
                item.isChecked = !item.isChecked;
                holder.checkBox.setChecked(item.isChecked);
                // 可以在这里添加长按后的其他逻辑
                return true;
            });

            return convertView;
        }

        private class ViewHolder {
            TextView titleTextView;
            TextView timeTextView;
            CheckBox checkBox;
        }
    }
}