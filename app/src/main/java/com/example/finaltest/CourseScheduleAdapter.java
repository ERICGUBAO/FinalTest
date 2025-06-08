package com.example.finaltest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CourseScheduleAdapter extends RecyclerView.Adapter<CourseScheduleAdapter.ViewHolder> {

    private final List<CourseItem> courseItems;

    public CourseScheduleAdapter(List<CourseItem> courseItems) {
        this.courseItems = courseItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseItem item = courseItems.get(position);
        holder.courseName.setText(item.courseName);
        holder.timeText.setText(item.time);
        holder.teacherText.setText(item.teacher);
        holder.classroomText.setText(item.classroom);

        // 设置颜色标记
        holder.colorIndicator.setBackgroundColor(item.color);

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            // 实现课程编辑功能
            EditCourseDialog dialog = new EditCourseDialog(v.getContext(), item,
                    (updatedItem) -> {
                        courseItems.set(position, updatedItem);
                        notifyItemChanged(position);
                    });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return courseItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView courseName;
        TextView timeText;
        TextView teacherText;
        TextView classroomText;
        View colorIndicator;

        public ViewHolder(View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            timeText = itemView.findViewById(R.id.course_time);
            teacherText = itemView.findViewById(R.id.course_teacher);
            classroomText = itemView.findViewById(R.id.course_classroom);
            colorIndicator = itemView.findViewById(R.id.color_indicator);
        }
    }
}