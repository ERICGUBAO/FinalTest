package com.example.finaltest;

import android.content.Context;
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
    private static final int PERIODS_PER_DAY = 12; // 每天12节课
    private static final int TIME_COLUMN = 0;
    private static final int ITEM_HEIGHT_DP = 80; // 每个格子的高度（dp）

    public CourseScheduleAdapter(List<CourseItem> courseItems, Context context) {
        this.courseItems = courseItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_schedule, parent, false);

        // 设置固定高度（避免无限滚动）
        int heightPx = (int) (ITEM_HEIGHT_DP * context.getResources().getDisplayMetrics().density);
        view.getLayoutParams().height = heightPx;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int column = position % 8; // 8列（时间列+7天）
        int row = position / 8;

        if (column == TIME_COLUMN) {
            // 时间列：显示节数
            holder.courseName.setText(String.format("%d-%d节", row + 1, row + 2));
            holder.courseName.setTextColor(context.getColor(android.R.color.darker_gray));
            holder.itemView.setBackgroundResource(R.drawable.bg_time_column);
            holder.itemView.setVisibility(View.VISIBLE);
        } else {
            // 课程列：查找是否有课程
            CourseItem course = findCourseAt(column - 1, row);
            if (course != null) {
                holder.courseName.setText(course.courseName);
                holder.courseTime.setText("时间：" + course.time);
                holder.courseClassroom.setText("教室：" + course.classroom);
                holder.courseTeacher.setText("教师：" + course.teacher);
                holder.cardView.setCardBackgroundColor(course.color);
                holder.itemView.setVisibility(View.VISIBLE);

                // 计算课程占用的行数
                int duration = getDuration(course.time);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height = (int) (ITEM_HEIGHT_DP * duration * context.getResources().getDisplayMetrics().density);
                holder.itemView.setLayoutParams(params);
            } else {
                // 隐藏空格子，但保留占位
                holder.itemView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private CourseItem findCourseAt(int dayOfWeek, int period) {
        for (CourseItem course : courseItems) {
            int courseDay = getDayOrder(course.time);
            int coursePeriod = getPeriod(course.time);
            int courseDuration = getDuration(course.time);

            // 检查课程是否在当前位置显示
            if (courseDay == dayOfWeek && period >= coursePeriod && period < coursePeriod + courseDuration) {
                // 只在课程开始的格子显示
                return (period == coursePeriod) ? course : null;
            }
        }
        return null;
    }

    // 将星期转换为列索引（0-6）
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

    // 计算课程开始的行索引
    private int getPeriod(String time) {
        String periodStr = time.split("\\s+")[1].split("-")[0];
        int hour = Integer.parseInt(periodStr.split(":")[0]);
        return hour - 8; // 假设8点对应第0行
    }

    // 计算课程持续的行数
    private int getDuration(String time) {
        String[] times = time.split("\\s+")[1].split("-");
        int startHour = Integer.parseInt(times[0].split(":")[0]);
        int endHour = Integer.parseInt(times[1].split(":")[0]);
        return (endHour - startHour) * 2; // 每小时2行（90分钟/行）
    }

    @Override
    public int getItemCount() {
        return DAYS_IN_WEEK * PERIODS_PER_DAY + PERIODS_PER_DAY; // 总格子数
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