package com.example.kcb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GeenFox on 2018/12/8.
 */

public class PostUtils extends AppCompatActivity{
    private static Map<String,String> cookies = null;
    public static String LOGIN_URL = "http://219.244.71.105/loginAction.do";
    public static String LoginByPost(String studentName, String studentPwd, String code, Map<String, String> cookies)
    {
        String msg = "";
        Connection connection = Jsoup.connect(LOGIN_URL);
        connection.timeout(15*1000);
        connection.cookies(cookies);
        System.out.println("**********************************************Cookies: ");
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            System.out.println(entry.getKey() + "-" + entry.getValue());
        }
        try{
            //这里可以写一些请求头的东西
            String data = "zjh:"+ URLEncoder.encode(studentName, "UTF-8")+
                    "mm:"+ URLEncoder.encode(studentPwd, "UTF-8")+"v_yzm;"+ URLEncoder.encode(code, "UTF-8");
            //System.out.println(data);
            Map<String,String> data1 = new HashMap<>();
            data1.put("zjh","41612248");
            data1.put("mm","xm1995@176.com");
            data1.put("v_yzm",code);

            //connection.data(data);
            connection.data(data1);
            Connection.Response resLogin = connection.ignoreContentType(true).method(Connection.Method.POST).execute();
            //cookies = resLogin.cookies();
            System.out.println(resLogin.headers());
            String body = resLogin.body();
            System.out.println(body);
            //我们请求的数据:
            /*OutputStream out = conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();*/
            /*if (conn.getResponseCode() == 200) {
                // 获取响应的输入流对象
                System.out.println("登陆成功");
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                msg = new String(message.toByteArray());
                System.out.println(msg);
                return msg;
            }*/
        }catch(Exception e){e.printStackTrace();}
        return msg;
    }
}
