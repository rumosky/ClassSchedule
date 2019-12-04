package com.example.kcb;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.kcb.Login.getLocalBitmap;

public class MainActivity extends AppCompatActivity {
    //当前头像
    private de.hdodenhof.circleimageview.CircleImageView icon;
    // 头像Bitmap
    private Bitmap head;
   // private static String path=Environment.getExternalStorageDirectory().getAbsolutePath()+"/kcb";
   // sd路径
    private static String path = "sdcard/myHead/";
    //滑动菜单
    private DrawerLayout mDrawerLayout;
    //星期几
    private RelativeLayout day;
    //当前日期
    protected TextView dateTextView;
    //toast事件
    public static Toast mToast;
    //SQLite Helper类
    private DatabaseHelper databaseHelper = new DatabaseHelper
            (this, "database.db", null, 1);

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //左边侧滑菜单
        mDrawerLayout = (DrawerLayout)findViewById(R.id.draw);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        //工具条
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.score);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                switch (item.getItemId()){
                    case R.id.add_courses:
                        Intent intent = new Intent(MainActivity.this, AddCourseActivity.class);
                        startActivityForResult(intent, 0);
                        break;
                    case R.id.score:
                        Intent intent0 = new Intent(MainActivity.this, Score.class);
                        startActivity(intent0);
                        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                        break;
                    case R.id.menu_about:
                        Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.login:
                        Intent intent2 = new Intent(MainActivity.this,Login.class);
                        startActivity(intent2);
                        break;
                    case R.id.single_week:
                        Intent intent3 = new Intent(MainActivity.this,SingleWeek.class);
                        startActivityForResult(intent3, 0);
                        break;
                    case R.id.double_week:
                        Intent intent4 = new Intent(MainActivity.this,DoubleWeek.class);
                        startActivityForResult(intent4, 0);
                        break;
                    default:
                        System.out.println("错误");
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        //创建课程表左边视图(节数)
        createLeftView();
        //从数据库读取数据
        loadData();
        //获取系统时间
        dateTextView = (TextView) navView.getHeaderView(0).findViewById(R.id.Menu_main_textDate);
        //获取头像
        icon = (de.hdodenhof.circleimageview.CircleImageView) navView.getHeaderView(0).findViewById(R.id.icon_image);
        //初始化日期与头像
        initDate();
        initView();
        //相机拍摄不成功时，加上这三行代码
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    //从数据库加载数据
    private void loadData() {
        ArrayList<Course> coursesList = new ArrayList<>(); //课程列表
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from courses", null);
        if (cursor.moveToFirst()) {
            do {
                coursesList.add(new Course(
                        cursor.getString(cursor.getColumnIndex("course_name")),
                        cursor.getString(cursor.getColumnIndex("teacher")),
                        cursor.getString(cursor.getColumnIndex("class_room")),
                        cursor.getInt(cursor.getColumnIndex("day")),
                        cursor.getInt(cursor.getColumnIndex("class_start")),
                        cursor.getInt(cursor.getColumnIndex("class_end")),
                        cursor.getString(cursor.getColumnIndex("week"))));
            } while(cursor.moveToNext());
        }
        cursor.close();

        //使用从数据库读取出来的课程信息来加载课程表视图
        for (Course course : coursesList) {
            createCourseView(course);
        }
    }

    //保存数据到数据库
    private void saveData(Course course) {
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL
                ("insert into courses(course_name, teacher, class_room, day, class_start, class_end, week) " + "values(?, ?, ?, ?, ?, ?, ?)",
                        new String[] {course.getCourseName(),
                                course.getTeacher(),
                                course.getClassRoom(),
                                course.getDay()+"",
                                course.getStart()+"",
                                course.getEnd()+"",
                                course.getWeek()}
                );
    }

    //创建课程节数视图
    private void createLeftView() {
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
    private void createCourseView(final Course course) {
            final int[] course_bj = {R.drawable.coursetable1, R.drawable.coursetable2,
                    R.drawable.coursetable3, R.drawable.coursetable4, R.drawable.coursetable5,
                    R.drawable.coursetable6, R.drawable.coursetable7, R.drawable.coursetable8,
                    R.drawable.coursetable9, R.drawable.coursetable10};
            int height = 180;
            int getDay = course.getDay();
            if ((getDay < 1 || getDay > 7) || course.getStart() > course.getEnd() || course.getEnd() > 10) {
                if (mToast == null) {
                    mToast = Toast.makeText(this, "星期几输错,或课程节次输错，请重新输入", Toast.LENGTH_SHORT);
                } else {
                    mToast.setText("星期几输错,或课程节次输错，请重新输入");
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            } else {
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
                v.setY(height * (course.getStart() - 1)); //设置开始高度,即第几节课开始
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, (course.getEnd() - course.getStart() + 1) * height - 8); //设置布局高度,即跨多少节课
                v.setLayoutParams(params);
                TextView text = (TextView) v.findViewById(R.id.text_view);
                text.setBackgroundResource(course_bj[(int) (Math.random() * 10)]);
                text.setText(course.getCourseName() + "\n\n" + course.getClassRoom()); //显示课程名
                day.addView(v);
                //长按删除课程
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        v.setVisibility(View.GONE);//先隐藏
                        day.removeView(v);//再移除课程视图
                        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
                        sqLiteDatabase.execSQL("delete from courses where course_name = ?", new String[]{course.getCourseName()});
                        return true;
                    }
                });
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MessageCourseActivity.class);
                        intent.putExtra("course", course);
                        startActivity(intent);
                    }
                });
            }
    }

    public void createLoginView(){
        final View v = LayoutInflater.from(this).inflate(R.layout.login, null); //加载登陆布局
        v.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //从相册里面取相片的返回结果
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }

                break;
            //调用系统裁剪图片后
            case 2:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    head = extras.getParcelable("data");

                    if (head != null) {
                        setPicToView(head);// 保存在SD卡中
                        icon.setImageBitmap(head);// 用CicleImageView显示出来
                    }
                }
                break;
            //调用拍摄功能
            case 3:
                if (resultCode == RESULT_OK) {
                    File temp = new File(Environment.getExternalStorageDirectory() + "/profile.jpg");
                    cropPhoto(Uri.fromFile(temp));// 裁剪图片
                }

                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0 && data != null) {
            Course course = (Course) data.getSerializableExtra("course");
            //创建课程表视图
            createCourseView(course);
            //创建登录视图
            createLoginView();
            //存储数据到数据库
            saveData(course);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }*/
    public void test_click(View v){
        Intent intent5 = new Intent(MainActivity.this, AddCourseActivity.class);
        startActivityForResult(intent5, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
        @SuppressLint("SimpleDateFormat")
        private void initDate() {
            Date currentTime = new Date();
            String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentTime);
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)  w = 0;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日  ");
            String dateString = formatter.format(currentTime);
            dateTextView.setText(dateString + "星期" + weekDays[w]);
        }
     public void change_image(View v){
         initView();
         showTypeDialog();
      }

    private void initView() {
        Bitmap bt = Login.getLocalBitmap(path+"profile.jpg");
        if (bt != null) {
            icon.setImageBitmap(bt);
        } else {
            /**
             * 如果SD里面没有则需要从服务器取头像，取回来的头像再保存在SD中
             *
             */
        }
    }

    private void showTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View change = LayoutInflater.from(this).inflate(R.layout.change_user_image, null);
        TextView select_gallery = (TextView) change.findViewById(R.id.select_gallery);
        TextView select_camera = (TextView) change.findViewById(R.id.select_camera);
        select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent changepho = new Intent(Intent.ACTION_PICK, null);
                changepho.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(changepho,1);
                dialog.dismiss();
            }
        });
        select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent changecam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                changecam.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "profile.jpg")));
                startActivityForResult(changecam, 3);
                dialog.dismiss();
            }
        });
        dialog.setView(change);
        dialog.show();
        initView();
    }

    /**
     * 调用系统的裁剪功能
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }

    private void setPicToView(Bitmap mBitmap) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(path);
        file.mkdirs();// 创建文件夹
        String fileName = path + "profile.jpg";// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


