package com.example.dai.lol.myComponent;
import android.renderscript.ScriptGroup;
import android.util.Log;


import com.alibaba.fastjson.JSON;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by dai on 2015/12/5.
 */
public class MyUrlHttp {
    //基本url
    private URL Base_Url;

    private String Login_Url = "http://10.139.49.212:5000/login";
    //private String Login_Url = "http://10.139.33.94:5000/login";

    private String Register_Url = "http://10.139.49.212:5000/register";

    private String getMovie_Url = "http://10.139.49.212:5000/getMovie";

    //声明httpurlconnection
    private HttpURLConnection conn ;

    //超时时间
    private int Time = 3*1000;

    //测试网络是否连接到服务器
    public boolean pingNet(){
        try {
            Base_Url = new URL(Login_Url);
            Log.v("Test","url");
            conn = (HttpURLConnection) Base_Url.openConnection();
            Log.v("Test","open");
            conn.setRequestMethod("GET");
            Log.v("Test","get");
            conn.setConnectTimeout(Time);
            conn.setReadTimeout(Time);
            Log.v("Test","settime");
            int responsecode = conn.getResponseCode();
            Log.v("Test","response");
            if(responsecode == HttpURLConnection.HTTP_OK){
                Log.v("Test","conn");
                conn.disconnect();
                return true;
            }
            else{
                return false;
            }
        }catch (Exception e){
            Log.v("Test", e.toString());
            return false;
        }
    }

    /**
     * get方式从服务器获取信息
     * String url 表示获取信息的Url
     */
    public String GetFromNet(String url){
        try {
//            switch(url){
//                case "login":Base_Url = new URL(Login_Url);break;
//                case "register":Base_Url = new URL(Register_Url);break;
//                case "getmovie":Base_Url = new URL(getMovie_Url);break;
//                default : Base_Url = new URL(Login_Url);break;
//            }
            String enurl = "http://10.139.49.212:5000/"+url;
            Base_Url = new URL(enurl);
            conn = (HttpURLConnection) Base_Url.openConnection();
            Log.v("TestGet","open");
            conn.setRequestMethod("GET");
            Log.v("TestGet","get");
            conn.setConnectTimeout(Time);
            conn.setReadTimeout(Time);
            Log.v("TestGet","settime");
            int responsecode = conn.getResponseCode();
            Log.v("TestGet","response");
            if(responsecode == HttpURLConnection.HTTP_OK){
                Log.v("TestGet","conn");
                InputStream input = conn.getInputStream();
                byte[] bs = new byte[2048];
                int len = 0;
                StringBuffer sb = new StringBuffer();
                while ((len = input.read(bs)) != -1) {
                    String str = new String(bs, 0, len);
                    sb.append(str);
                }
                Log.v("TestGet",sb.toString());
                conn.disconnect();
                return sb.toString();
            }
            else{
                return null;
            }
        }catch (Exception e){
            Log.v("TestGet", e.toString());
            return null;
        }
    }

    /**
     * post方法，传递json
     */
    public String PostNet(String url,JSON postdata){
        try {
            switch (url) {
                case "login":
                    Base_Url = new URL(Login_Url);
                    break;
                case "register":
                    Base_Url = new URL(Register_Url);
                    break;
                default:
                    Base_Url = new URL(Login_Url);
                    break;
            }
            //test
            //Base_Url = new URL("http://10.139.33.94:5000/login");
            String testStr = "ha=ha&$&email=da&i@exam.com&password=dai";
            //testStr = URLEncoder.encode(testStr,"utf-8");
            byte[] test = testStr.getBytes("utf-8");

            conn = (HttpURLConnection) Base_Url.openConnection();
            Log.v("TestPOST", "post");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            //conn.setRequestProperty("Accept", "text/html, application/xhtml+xml, */*");
            //conn.setRequestProperty("Accept-Language","en-US,en;q=0.8,zh-Hans-CN;q=0.5,zh-Hans;q=0.3");
            //conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            //conn.setRequestProperty("Accept-Charset","UTF-8");
            //conn.setRequestProperty("Content-Type","application/x-java-serialized-object");
            conn.setRequestProperty("Content-Type","application/json");
            //conn.setRequestProperty("Content-Length",test.length+"");
            conn.setConnectTimeout(Time);
            Log.v("TestPOST", "set");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(conn.getOutputStream());
            //objectOutputStream.write(test);
            objectOutputStream.writeObject(postdata.toString());
            Log.v("TestPOST", postdata.toString());
            objectOutputStream.flush();
            Log.v("TestPOST", "flush");
            objectOutputStream.close();
            Log.v("TestPOST", "close");
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                Log.v("TestGet","conn");
                InputStream input = conn.getInputStream();
                byte[] bs = new byte[1024];
                int len = 0;
                StringBuffer sb = new StringBuffer();
                while ((len = input.read(bs)) != -1) {
                    String str = new String(bs, 0, len);
                    sb.append(str);
                }
                Log.v("TestGet", sb.toString());
                conn.disconnect();
                return sb.toString();
                //JSON json = JSON.parseObject(sb.toString());
                //return json;
            }
            else{
                return null;
            }
        }catch (Exception e){
            Log.v("TestGet", e.toString());
            return null;
        }
    }

}
