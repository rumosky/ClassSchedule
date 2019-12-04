package com.example.kcb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by LENOVO on 2018/12/18.
 */
public class MessageCourseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_course);
        setFinishOnTouchOutside(false);

        final TextView inputCourseName = (TextView) findViewById(R.id.course_name);
        final TextView inputTeacher = (TextView) findViewById(R.id.teacher_name);
        final TextView inputClassRoom = (TextView) findViewById(R.id.class_room);
        final TextView inputDay = (TextView) findViewById(R.id.day);
        final TextView inputTime = (TextView) findViewById(R.id.time_information);
        Intent intent = getIntent();
        Course course = (Course) intent.getSerializableExtra("course");
        inputCourseName.setText(course.getCourseName());
        inputTeacher.setText(course.getTeacher());
        inputClassRoom.setText(course.getClassRoom());
        inputDay.setText(course.getWeek()+"周" +getDayString(course.getDay()));
        inputTime.setText(course.getStart()+"-" + course.getEnd()+ "节");
        Button backButton = (Button) findViewById(R.id.button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    static public String getDayString(int day) {//由数字星期得出文字星期
        String dayString;
        switch (day){
            case 1:
                dayString = "一";
                break;
            case 2:
                dayString = "二";
                break;
            case 3:
                dayString = "三";
                break;
            case 4:
                dayString = "四";
                break;
            case 5:
                dayString = "五";
                break;
            case 6:
                dayString= "六";
                break;
            case 7:
                dayString= "日";
                break;
            default:
                dayString= "";
                break;
        }
        return dayString;
    }
}
