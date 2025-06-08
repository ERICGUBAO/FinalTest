package com.example.finaltest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class AddCourseDialog extends Dialog {

    public interface OnCourseAddedListener {
        void onCourseAdded(String name, String teacher, String room, String time, int color);
    }

    private final OnCourseAddedListener listener;
    private int selectedColor = Color.BLUE;

    public AddCourseDialog(@NonNull Context context, OnCourseAddedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_course);

        EditText nameEdit = findViewById(R.id.edit_course_name);
        EditText teacherEdit = findViewById(R.id.edit_teacher);
        EditText roomEdit = findViewById(R.id.edit_classroom);
        Spinner daySpinner = findViewById(R.id.spinner_day);
        Spinner timeSpinner = findViewById(R.id.spinner_time);
        Button colorButton = findViewById(R.id.button_color);
        Button saveButton = findViewById(R.id.button_save);

        // 初始化下拉菜单
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.days_of_week, android.R.layout.simple_spinner_item);
        daySpinner.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.class_times, android.R.layout.simple_spinner_item);
        timeSpinner.setAdapter(timeAdapter);

        // 颜色选择
        colorButton.setOnClickListener(v -> showColorPicker());

        // 保存课程
        saveButton.setOnClickListener(v -> {
            String name = nameEdit.getText().toString();
            String teacher = teacherEdit.getText().toString();
            String room = roomEdit.getText().toString();
            String time = daySpinner.getSelectedItem() + " " + timeSpinner.getSelectedItem();

            if (!name.isEmpty()) {
                listener.onCourseAdded(name, teacher, room, time, selectedColor);
                dismiss();
            } else {
                Toast.makeText(getContext(), "请输入课程名称", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showColorPicker() {
        ColorPickerDialogBuilder
                .with(getContext())
                .setTitle("选择课程颜色")
                .initialColor(selectedColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("确定", (dialog, selectedColor, allColors) -> {
                    this.selectedColor = selectedColor;
                    findViewById(R.id.button_color).setBackgroundColor(selectedColor);
                })
                .setNegativeButton("取消", (dialog, which) -> {})
                .build()
                .show();
    }
}