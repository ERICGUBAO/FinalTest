package com.example.finaltest;

import android.os.Parcel;
import android.os.Parcelable;

public class CourseItem implements Parcelable {
    public String courseName;
    public String teacher;
    public String classroom;
    public String time;
    public int color;

    public CourseItem(String courseName, String teacher, String classroom, String time, int color) {
        this.courseName = courseName;
        this.teacher = teacher;
        this.classroom = classroom;
        this.time = time;
        this.color = color;
    }

    protected CourseItem(Parcel in) {
        courseName = in.readString();
        teacher = in.readString();
        classroom = in.readString();
        time = in.readString();
        color = in.readInt();
    }

    public static final Creator<CourseItem> CREATOR = new Creator<CourseItem>() {
        @Override
        public CourseItem createFromParcel(Parcel in) {
            return new CourseItem(in);
        }

        @Override
        public CourseItem[] newArray(int size) {
            return new CourseItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(courseName);
        dest.writeString(teacher);
        dest.writeString(classroom);
        dest.writeString(time);
        dest.writeInt(color);
    }
}