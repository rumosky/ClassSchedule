package com.example.kcb;


import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class CookieUtil {

    public static void main(String[] args) {

        URLConnection conn;
        try {
            URL obj = new URL("http://219.244.71.105/LoginAction.do");
            conn = obj.openConnection();
            Map<String, List<String>> cookies = conn.getHeaderFields();
            List<String> setCookies = cookies.get("Set-Cookie");
            System.out.println("显示响应cookie信息..."+setCookies);
            /*.saveCookiePreference(this,setCookies);*/
            /*String cookie = "JSESSIONID="+setCookies+";"+"key1="+";key2=value2";
            conn.setRequestProperty("Cookie", cookie);*/
        } catch (IOException e)
        {e.printStackTrace();}
    }

    public static final String ISLOGINED = "islogined";
    public static final String COOKIE = "cookie";
    public static void saveCookiePreference(Context context, String value) {
        SharedPreferences preference = context.getSharedPreferences(ISLOGINED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putString(COOKIE, value);
        editor.apply();
    }
    public static String getCookiePreference(Context context) {
        SharedPreferences preference = context.getSharedPreferences(ISLOGINED, Context.MODE_PRIVATE);
        String s = preference.getString(COOKIE, "");
        return s;
    }
}