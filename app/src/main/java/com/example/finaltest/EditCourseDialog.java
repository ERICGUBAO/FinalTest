package com.example.finaltest;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class EditCourseDialog extends Dialog {

    public interface OnCourseUpdatedListener {
        void onCourseUpdated(CourseItem updatedItem);
    }

    private final CourseItem courseItem;
    private final OnCourseUpdatedListener listener;

    public EditCourseDialog(Context context, CourseItem courseItem, OnCourseUpdatedListener listener) {
        super(context);
        this.courseItem = courseItem;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_course);

        EditText nameEdit = findViewById(R.id.edit_course_name);
        EditText teacherEdit = findViewById(R.id.edit_teacher);
        EditText roomEdit = findViewById(R.id.edit_classroom);
        EditText timeEdit = findViewById(R.id.edit_time);

        nameEdit.setText(courseItem.courseName);
        teacherEdit.setText(courseItem.teacher);
        roomEdit.setText(courseItem.classroom);
        timeEdit.setText(courseItem.time);

        findViewById(R.id.button_save).setOnClickListener(v -> {
            String name = nameEdit.getText().toString();
            String teacher = teacherEdit.getText().toString();
            String room = roomEdit.getText().toString();
            String time = timeEdit.getText().toString();

            if (!name.isEmpty()) {
                CourseItem updatedItem = new CourseItem(name, teacher, room, time, courseItem.color);
                listener.onCourseUpdated(updatedItem);
                dismiss();
            } else {
                Toast.makeText(getContext(), "请输入课程名称", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.button_cancel).setOnClickListener(v -> dismiss());
    }
}