// CourseItem.java
package com.example.finaltest;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

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

    // 将CourseItem转换为JSON字符串
    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("courseName", courseName);
            jsonObject.put("teacher", teacher);
            jsonObject.put("classroom", classroom);
            jsonObject.put("time", time);
            jsonObject.put("color", color);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    // 从JSON字符串创建CourseItem
    public static CourseItem fromJson(String jsonStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            return new CourseItem(
                    jsonObject.getString("courseName"),
                    jsonObject.getString("teacher"),
                    jsonObject.getString("classroom"),
                    jsonObject.getString("time"),
                    jsonObject.getInt("color")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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