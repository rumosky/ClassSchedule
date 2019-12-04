package com.example.kcb;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by LENOVO on 2018/12/22.
 */

public class SingleWeek extends AppCompatActivity {
    //星期几
    private RelativeLayout day;

    //SQLite Helper类
    private DatabaseHelper databaseHelper = new DatabaseHelper
            (this, "database.db", null, 1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_week);
        createweekLeftView();
        loadweekData();
        Button backButton = (Button) findViewById(R.id.back_single);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //从数据库加载数据
    private void loadweekData() {
        ArrayList<Course> coursesList = new ArrayList<>(); //课程列表
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        Cursor cursor_week = sqLiteDatabase.rawQuery("select * from courses", null);
        if (cursor_week.moveToFirst()) {
            do {
                coursesList.add(new Course(
                        cursor_week.getString(cursor_week.getColumnIndex("course_name")),
                        cursor_week.getString(cursor_week.getColumnIndex("teacher")),
                        cursor_week.getString(cursor_week.getColumnIndex("class_room")),
                        cursor_week.getInt(cursor_week.getColumnIndex("day")),
                        cursor_week.getInt(cursor_week.getColumnIndex("class_start")),
                        cursor_week.getInt(cursor_week.getColumnIndex("class_end")),
                        cursor_week.getString(cursor_week.getColumnIndex("week"))));
            } while(cursor_week.moveToNext());
        }
        cursor_week.close();

        //使用从数据库读取出来的课程信息来加载课程表视图
        for (Course course_week : coursesList) {
            if(course_week.getWeek().equals("双周")==false)
            {createweekCourseView(course_week);}
        }
    }
    //创建课程节数视图
    private void createweekLeftView() {
        for(int i=1;i<11;i++)
        {
            View view = LayoutInflater.from(this).inflate(R.layout.left_view, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(110,180);
            view.setLayoutParams(params);
            TextView text = (TextView) view.findViewById(R.id.class_number_text);
            text.setText(String.valueOf(i));
            LinearLayout leftViewLayout = (LinearLayout) findViewById(R.id.left_view_layout);
            leftViewLayout.addView(view);
        }
    }
    //创建课程视图
    private void createweekCourseView(final Course course_week) {
        final int[] course_bj = {R.drawable.coursetable1, R.drawable.coursetable2,
                R.drawable.coursetable3, R.drawable.coursetable4, R.drawable.coursetable5,
                R.drawable.coursetable6, R.drawable.coursetable7, R.drawable.coursetable8,
                R.drawable.coursetable9, R.drawable.coursetable10};
        int height = 180;
        int getDay = course_week.getDay();
            switch (getDay) {
                case 1:
                    day = (RelativeLayout) findViewById(R.id.monday);
                    break;
                case 2:
                    day = (RelativeLayout) findViewById(R.id.tuesday);
                    break;
                case 3:
                    day = (RelativeLayout) findViewById(R.id.wednesday);
                    break;
                case 4:
                    day = (RelativeLayout) findViewById(R.id.thursday);
                    break;
                case 5:
                    day = (RelativeLayout) findViewById(R.id.friday);
                    break;
                case 6:
                    day = (RelativeLayout) findViewById(R.id.saturday);
                    break;
                case 7:
                    day = (RelativeLayout) findViewById(R.id.weekday);
                    break;
            }
                final View v = LayoutInflater.from(this).inflate(R.layout.course_card, null); //加载单个课程布局
                v.setY(height * (course_week.getStart() - 1)); //设置开始高度,即第几节课开始
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, (course_week.getEnd() - course_week.getStart() + 1) * height - 8); //设置布局高度,即跨多少节课
                v.setLayoutParams(params);
                TextView text = (TextView) v.findViewById(R.id.text_view);
                text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                text.setText(course_week.getCourseName() + "\n\n" + course_week.getClassRoom()); //显示课程名
                day.addView(v);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SingleWeek.this, MessageCourseActivity.class);
                        intent.putExtra("course", course_week);
                        startActivity(intent);
                    }
                });
    }
}
