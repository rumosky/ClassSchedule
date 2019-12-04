package com.example.kcb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * Created by Lenovo on 2018/12/2.
 */

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String codeURL = "http://http://219.244.71.105/validateCodeAction.do?";
        codeURL += Math.random();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        final DownLoadThread dlt = new DownLoadThread(codeURL);
        new Thread(dlt).start();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/kcb");
        ImageView img = (ImageView) findViewById(R.id.img_code_url);
        Bitmap bitmap = getLocalBitmap(file+"/img_code.jpg");
        img.setImageBitmap(bitmap);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText inputStudentName = (EditText) findViewById(R.id.student_id);
                final EditText inputStudentPwd = (EditText) findViewById(R.id.student_pwd);
                final EditText inputCode = (EditText) findViewById(R.id.img_code);
                final String studentName = String.valueOf(inputStudentName.getText());
                final String studentPwd = String.valueOf(inputStudentPwd.getText());
                final String code = String.valueOf(inputCode.getText());
                if(studentName.equals("")||studentPwd.equals("")||code.equals("")){
                    Toast.makeText(Login.this, "账号信息未填写完整", Toast.LENGTH_SHORT).show();
                }else {
                    PositThread PT = new PositThread(studentName,studentPwd,code,dlt.getCookies());
                    new Thread(PT).start();
                }
            }
        });
    }

    public static Bitmap getLocalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class DownLoadThread implements Runnable {
        private String imgUrl;
        private Map<String,String> cookies = new HashMap<>();

        public DownLoadThread(String url) {
            this.imgUrl = url;
        }

        public Map<String,String> getCookies(){
            return cookies;
        }

        @Override
        public void run() {
            System.out.println("线程开始运行");
            String indexURL = "http://219.244.71.105/login.jsp";
            FileOutputStream out = null;
            BufferedOutputStream bos = null;
            //HttpURLConnection conn = null;
            Connection connection = Jsoup.connect(imgUrl);
            String fileName = "/img_code.jpg";
            try {
                org.jsoup.Connection connect = Jsoup.connect(indexURL);
                Connection.Response resIndex = connect.ignoreContentType(true).method(Connection.Method.GET).execute();
                this.cookies = resIndex.cookies();
                System.out.println("**********************************************Cookies: ");
                for (Map.Entry<String, String> entry : cookies.entrySet()) {
                    System.out.println(entry.getKey() + "-" + entry.getValue());
                }
                URL url = new URL(imgUrl);
                connection.cookies(cookies);
                connection.timeout(30*1000);
                Connection.Response  resImg =  connection.ignoreContentType(true).execute();
                byte []img = resImg.bodyAsBytes();
                /* conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(50 * 1000);
                InputStream inputStream = conn.getInputStream();*/
                //InputStream inputStream = connection.getInputStream();
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/kcb");
                //File file = new File("E:/Android/kcb/app/src/main/res/drawable/sd_bg.jpg");
                String state = Environment.getExternalStorageState();
                if(state.equals(Environment.MEDIA_MOUNTED))
                    System.out.println("可读取");
                if(file.exists()){
                    System.out.println(file.toString()+"存在");
                }else {
                    System.out.println(file.toString()+"不存在");
                    file.mkdirs();
                }
                out = new FileOutputStream(new File(file+fileName));
                bos = new BufferedOutputStream(out);
                bos.write(img);
                System.out.println("=====处理完成====");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("图片下载失败");
            } finally {
                System.out.println("线程运行结束");
                if(bos != null){
                    try{
                        bos.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static class PositThread implements Runnable{
        private String sName;
        private String sPwd;
        private String sCode;
        private Map<String,String> cookies;
        public PositThread(String name,String pwd,String code,Map<String,String> cookies){
            this.sName = name;
            this.sPwd = pwd;
            this.sCode = code;
            this.cookies = cookies;
        }
        @Override
        public void run() {
            PostUtils.LoginByPost(sName,sPwd,sCode,cookies);
        }
    }
}
