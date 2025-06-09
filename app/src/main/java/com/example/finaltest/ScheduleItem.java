package com.example.finaltest;

import java.io.Serializable;

public class ScheduleItem implements Serializable {
    public String title;
    public String time;
    public String date;
    public boolean isChecked;

    public ScheduleItem(String title, String time, String date, boolean isChecked) {
        this.title = title;
        this.time = time;
        this.date = date;
        this.isChecked = isChecked;
    }
}