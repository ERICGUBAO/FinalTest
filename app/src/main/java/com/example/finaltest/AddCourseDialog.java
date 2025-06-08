package com.example.finaltest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

public class AddCourseDialog extends Dialog {

    public interface OnCourseAddedListener {
        void onCourseAdded(CourseItem courseItem);  // 修改为接收CourseItem对象
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

        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.days_of_week, android.R.layout.simple_spinner_item);
        daySpinner.setAdapter(dayAdapter);

        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.class_times, android.R.layout.simple_spinner_item);
        timeSpinner.setAdapter(timeAdapter);

        colorButton.setOnClickListener(v -> showColorPicker());

        saveButton.setOnClickListener(v -> {
            String name = nameEdit.getText().toString();
            String teacher = teacherEdit.getText().toString();
            String room = roomEdit.getText().toString();
            String time = daySpinner.getSelectedItem() + " " + timeSpinner.getSelectedItem();

            if (!name.isEmpty()) {
                CourseItem newCourse = new CourseItem(name, teacher, room, time, selectedColor);
                listener.onCourseAdded(newCourse);  // 传递CourseItem对象
                dismiss();
            } else {
                Toast.makeText(getContext(), "请输入课程名称", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showColorPicker() {
        View colorPickerView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_color_picker, null);
        SeekBar redSeekBar = colorPickerView.findViewById(R.id.seekbar_red);
        SeekBar greenSeekBar = colorPickerView.findViewById(R.id.seekbar_green);
        SeekBar blueSeekBar = colorPickerView.findViewById(R.id.seekbar_blue);
        View colorPreview = colorPickerView.findViewById(R.id.color_preview);

        redSeekBar.setProgress(Color.red(selectedColor));
        greenSeekBar.setProgress(Color.green(selectedColor));
        blueSeekBar.setProgress(Color.blue(selectedColor));
        colorPreview.setBackgroundColor(selectedColor);

        SeekBar.OnSeekBarChangeListener colorChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedColor = Color.rgb(
                        redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress()
                );
                colorPreview.setBackgroundColor(selectedColor);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        redSeekBar.setOnSeekBarChangeListener(colorChangeListener);
        greenSeekBar.setOnSeekBarChangeListener(colorChangeListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangeListener);

        new AlertDialog.Builder(getContext())
                .setTitle("选择课程颜色")
                .setView(colorPickerView)
                .setPositiveButton("确定", (dialog, which) -> {
                    findViewById(R.id.button_color).setBackgroundColor(selectedColor);
                })
                .setNegativeButton("取消", null)
                .show();
    }
}