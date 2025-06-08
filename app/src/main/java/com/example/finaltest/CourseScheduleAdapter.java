package com.example.finaltest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CourseScheduleAdapter extends RecyclerView.Adapter<CourseScheduleAdapter.ViewHolder> {
    private final List<CourseItem> courseItems;
    private final Context context;
    private static final int DAYS_IN_WEEK = 7;
    private static final int PERIODS_PER_DAY = 12;
    private static final int TIME_COLUMN = 0;
    private static final int ITEM_HEIGHT_DP = 80;

    public CourseScheduleAdapter(List<CourseItem> courseItems, Context context) {
        this.courseItems = courseItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_schedule, parent, false);

        // 设置固定高度
        int heightPx = (int) (ITEM_HEIGHT_DP * context.getResources().getDisplayMetrics().density);
        view.getLayoutParams().height = heightPx;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int column = position % 8;
        int row = position / 8;

        if (column == TIME_COLUMN) {
            // 时间列：显示节数
            holder.courseName.setText(String.format("%d-%d节", row + 1, row + 2));
            holder.courseName.setTextColor(Color.DKGRAY);
            holder.itemView.setBackgroundResource(R.drawable.bg_time_column);
            holder.itemView.setVisibility(View.VISIBLE);

            // 隐藏课程详细信息
            holder.courseTime.setVisibility(View.GONE);
            holder.courseClassroom.setVisibility(View.GONE);
            holder.courseTeacher.setVisibility(View.GONE);
        } else {
            // 课程列：查找是否有课程
            CourseItem course = findCourseAt(column - 1, row);
            if (course != null) {
                // 解析时间字符串（只显示时间段，不显示星期几）
                String[] timeParts = course.time.split("\\s+");
                String timeDisplay = timeParts.length > 1 ? timeParts[1] : course.time;

                holder.courseName.setText(course.courseName);
                holder.courseTime.setText(timeDisplay);

                // 有教室时显示，没有时隐藏
                if (course.classroom != null && !course.classroom.isEmpty()) {
                    holder.courseClassroom.setText(course.classroom);
                    holder.courseClassroom.setVisibility(View.VISIBLE);
                } else {
                    holder.courseClassroom.setVisibility(View.GONE);
                }

                // 有教师时显示，没有时隐藏
                if (course.teacher != null && !course.teacher.isEmpty()) {
                    holder.courseTeacher.setText(course.teacher);
                    holder.courseTeacher.setVisibility(View.VISIBLE);
                } else {
                    holder.courseTeacher.setVisibility(View.GONE);
                }

                holder.cardView.setCardBackgroundColor(course.color);
                holder.itemView.setVisibility(View.VISIBLE);

                // 显示所有课程信息视图
                holder.courseTime.setVisibility(View.VISIBLE);
            } else {
                // 没有课程，隐藏格子
                holder.itemView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private CourseItem findCourseAt(int dayOfWeek, int period) {
        for (CourseItem course : courseItems) {
            int courseDay = getDayOrder(course.time);
            int coursePeriod = getPeriod(course.time);
            int courseDuration = getDuration(course.time);

            if (courseDay == dayOfWeek && period >= coursePeriod && period < coursePeriod + courseDuration) {
                return (period == coursePeriod) ? course : null;
            }
        }
        return null;
    }

    private int getDayOrder(String time) {
        String day = time.split("\\s+")[0];
        switch (day) {
            case "周一": return 0;
            case "周二": return 1;
            case "周三": return 2;
            case "周四": return 3;
            case "周五": return 4;
            case "周六": return 5;
            case "周日": return 6;
            default: return -1;
        }
    }

    private int getPeriod(String time) {
        String periodStr = time.split("\\s+")[1].split("-")[0];
        int hour = Integer.parseInt(periodStr.split(":")[0]);
        return hour - 8;
    }

    private int getDuration(String time) {
        String[] times = time.split("\\s+")[1].split("-");
        int startHour = Integer.parseInt(times[0].split(":")[0]);
        int endHour = Integer.parseInt(times[1].split(":")[0]);
        return (endHour - startHour);
    }

    @Override
    public int getItemCount() {
        return DAYS_IN_WEEK * PERIODS_PER_DAY + PERIODS_PER_DAY;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, courseTime, courseClassroom, courseTeacher;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            courseName = itemView.findViewById(R.id.course_name);
            courseTime = itemView.findViewById(R.id.course_time);
            courseClassroom = itemView.findViewById(R.id.course_classroom);
            courseTeacher = itemView.findViewById(R.id.course_teacher);
        }
    }
}