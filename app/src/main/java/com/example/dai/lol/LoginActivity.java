package com.example.dai.lol;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.example.dai.lol.myComponent.MyApplication;
import com.example.dai.lol.myComponent.MyUrlHttp;
import com.example.dai.lol.someuser.CurrentUser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dai on 2015/11/30.
 */
public class LoginActivity extends FragmentActivity
        implements View.OnTouchListener,GestureDetector.OnGestureListener, View.OnClickListener{
    //声明一个变量来保存全局Application
    protected MyApplication app;

    //出错信息用HashMap保存
    protected Map ErrorMessage;

    //定义手势检测实例
    public static GestureDetector detector;
    //定义手势两点之间的最小距离
    final int DISTANT=50;

    //定义控件
    private RelativeLayout register_tab;
    private RelativeLayout login_tab;
    private TextView logintext;
    private TextView registertext;
    private Fragment login_fragment;
    private Fragment register_fragment;

    //登录还是注册选项
    private boolean Login_Or_Register;

    //网络封装类
    private MyUrlHttp myUrlHttp;

    /**
     * 用HashMap定义出错信息
     */
    public void initErrorMessage(){
        ErrorMessage = new HashMap();
        ErrorMessage.put("Input_Empty", "未输入");
        ErrorMessage.put("Input_Vaild", "不匹配");
    }


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        findViews();
        //获取全局Application
        this.app = (MyApplication)getApplication();
        //初始化错误信息
        initErrorMessage();
        //创建手势检测器
        detector = new GestureDetector(LoginActivity.this, this);
        //布局监听滑动
        LinearLayout login_activity = (LinearLayout)findViewById(R.id.login_main);
        login_activity.setOnTouchListener(this);
        login_activity.setLongClickable(true);
        setTabSelection(0);
        //初始化MyUrlHttp
        myUrlHttp = new MyUrlHttp();
        }

    /**
     * 将事件绑定在登录和取消按钮上
     */
    private void findViews(){
        register_tab = (RelativeLayout)findViewById(R.id.register_tab);
        login_tab = (RelativeLayout)findViewById(R.id.login_tab);
        logintext = (TextView)findViewById(R.id.login_text);
        registertext = (TextView)findViewById(R.id.register_text);
        login_fragment = new LoginFragment();
        register_fragment =new RegisterFragment();
        //确认按钮
        Button confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        //取消按钮
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        //登录选项卡
        login_tab.setOnClickListener(this);
        register_tab.setOnClickListener(this);
    }

    /**
     * 处理手势注册
     */

    /**
     * 因为将主Activity出栈了，所以返回键会直接退出程序，所以将返回绑定到与取消按钮一样的逻辑上
     */
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            jumpBackToMain();
        }
        return true;
    }

    /**
     * 验证登录邮箱和密码是否正确
     * @param email passwd
     * @return bool
     */
    protected boolean confirmLogin(String email,String passwd){
        //如果是注册，则不用验证（暂时）
        if(!Login_Or_Register){
            EditText input_username = (EditText)findViewById(R.id.usernameEdittext);
            EditText input_city = (EditText)findViewById(R.id.cityEdittext);
            String username = input_username.getText().toString();
            String city = input_city.getText().toString();
            HashMap<String,String> data = new HashMap<String,String>();
            data.put("email", email);
            data.put("password", passwd);
            data.put("username",username);
            data.put("city", city);
            JSON postdata = JSON.parseObject(JSON.toJSONString(data));
            List<Object> li = new ArrayList<Object>();
            li.add("register");
            li.add(postdata);
            myLoginAsnytask task = new myLoginAsnytask();
            task.execute(li);
            return false;
        }
        //如果是登录
        else{

            if(email.equals("123") && passwd.equals("123")){
                return true;
            }
            else {
                //测试post
                HashMap data = new HashMap();
                data.put("email", email);
                data.put("password", passwd);
                String jsonStr = JSON.toJSONString(data);
                JSON postdata = JSON.parseObject(jsonStr);
                Log.v("fastjson", jsonStr);
                List<Object> li = new ArrayList<Object>();
                li.add("login");
                li.add(postdata);
                myLoginAsnytask task = new myLoginAsnytask();
                task.execute(li);
                return false;
            }
        }
    }

    /**
     * 处理输入框
     */
    protected boolean processInput(){
        EditText input_email = (EditText) findViewById(R.id.accountEdittext);
        EditText input_pwd = (EditText) findViewById(R.id.pwdEdittext);
        String email = input_email.getText().toString();
        String passwd = input_pwd.getText().toString();
        //如果邮箱输入为空
        if(TextUtils.isEmpty(email)){
            input_email.setError(makeErrorMessage("email","Input_Empty"));
            return false;
        }
        //如果密码为空
        if(TextUtils.isEmpty(passwd)) {
            input_pwd.setError(makeErrorMessage("passwd", "Input_Empty"));
            return false;
        }
        //如果是注册，则还需要验证昵称和城市
        if(!Login_Or_Register){
            EditText input_username = (EditText)findViewById(R.id.usernameEdittext);
            EditText input_city = (EditText)findViewById(R.id.cityEdittext);
            String username = input_username.getText().toString();
            String city = input_city.getText().toString();
            if(TextUtils.isEmpty(username)){
                input_username.setError(makeErrorMessage("username","Input_Empty"));
                return false;
            }
            if(TextUtils.isEmpty(city)){
                input_city.setError(makeErrorMessage("city","Input_Empty"));
                return false;
            }
        }
        //验证密码是否正确
        return confirmLogin(email,passwd);
    }

    /**
     * 生成错误信息
     * param String widget 出错的对象
     * param String error 出错的信息
     * @return String
     */
    protected String makeErrorMessage(String widget,String error){
        switch (widget){
            case "email":widget="电子邮箱";break;
            case "passwd":widget="密码";break;
            case "username":widget="昵称";break;
            case "city":widget="城市";break;
        }
        return widget+ErrorMessage.get(error);
    }

    /**
     * 跳转回主页，带回登陆信息
     */
    private void jumpBackToMain(){
            //将当前Activity移出栈
            LoginActivity.this.finish();
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
    }

    /**
     * 绑定点击事件，来确定选中登录还是注册
     */
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_tab:setTabSelection(0);break;
            case R.id.register_tab:setTabSelection(1);break;
            case R.id.cancel:jumpBackToMain();break;
            case R.id.confirm:processInput();break;
            default:break;
        }
    }


    /**
     * 设置当前fragment
     * int index 选中第几个标签
     */
    private void setTabSelection(int index){
        //取得FragmentManager的实体引用
        FragmentManager fm = getFragmentManager();
        //取得FragmentTransaction实体引用
        FragmentTransaction ft = fm.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况  
        //应该清除上次选中的标签的效果和fragment
        //ft.remove()
        switch(index){
            case 0:
                //选中登录
                Login_Or_Register = true;
                //改变另一侧relativelayout背景色
                register_tab.setBackgroundColor(Color.LTGRAY);
                // 当点击了消息tab时，改变控件的图片和文字颜色
                login_tab.setBackgroundColor(Color.WHITE);
                logintext.setTextColor(Color.BLACK);
                ft.replace(R.id.content, login_fragment);
                break;
            case 1:
                //选中注册
                Login_Or_Register = false;
                //改变另一侧relativelayout背景色
                login_tab.setBackgroundColor(Color.LTGRAY);
                // 当点击了消息tab时，改变控件的图片和文字颜色
                register_tab.setBackgroundColor(Color.WHITE);
                registertext.setTextColor(Color.BLACK);
                ft.replace(R.id.content, register_fragment);
                break;
        }
        ft.commit();
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.v("TEST","call touch");
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.v("TEST","call down");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.v("TEST","call showpress");

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.v("TEST","call tapup");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.v("TEST","call onscroll");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.v("TEST","call press");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.v("TEST","call fling");
        float x = 0;
        x = e2.getX() - e1.getX();
        //float y = e2.getY() - e1.getY();

        if (x > 0)
        {
            setTabSelection(1);
        }
        else if (x < 0)
        {
            setTabSelection(0);
        }
        return false;  
    }

    class myLoginAsnytask extends AsyncTask<List<Object>,Integer,String> {

        private ProgressBar pb;

        @Override
        protected String doInBackground(List<Object>... li) {
            List arr = li[0];
            String url = (String)arr.get(0);
            JSON postdata = (JSON)arr.get(1);
            String res = myUrlHttp.PostNet(url, postdata);
                    //解析JSON
                    //Map<String,String> data = (Map<String,String>) JSON.parse(msg);
                    //CurrentUser c = JSON.parseObject(res, CurrentUser.class);
                    //List<Map<String,String>> list = data.get("response");
                    //String version = list.get(0).get("name");
                    //Log.d("json", c.getCity());
            return res;
        }

        @Override
        protected void onPreExecute(){
            pb = new com.example.dai.lol.myComponent.myProgressBar(LoginActivity.this).progressBar;
            pb.setVisibility(View.VISIBLE);
            //progressBar.;
            Log.d("asynctask","callpre");
        }

        @Override
        protected void onPostExecute(String result){
            pb.setVisibility(View.GONE);
            if(TextUtils.isEmpty(result)){
                EditText input_pwd = (EditText) findViewById(R.id.pwdEdittext);
                input_pwd.setError("好像没联网？");
            } else{
                Log.d("asynctask", result);
                try {
                    Map<String, String> data = (Map<String, String>) JSON.parse(result);
                    String resp = data.get("response");
                    Log.d("response", resp);
                    //如果登陆或注册成功
                    if (resp.equals("comfirmed") || resp.equals("register ok")) {
                        //如果登陆成功
                        //将登录用户录入当前用户
                        CurrentUser c = JSON.parseObject(result, CurrentUser.class);
                        app.setCurrentUser(c);
                        //将登陆状态置为true
                        app.setLogin(true);
                        jumpBackToMain();
                    }
                    //登录失败
                    else {
                        if (resp.equals("email is not vaild")) {
                            TextView input_email = (TextView) findViewById(R.id.accountEdittext);
                            input_email.setError("账户不存在!");
                        } else if (resp.equals("password is wrong")) {
                            TextView input_passwd = (TextView) findViewById(R.id.pwdEdittext);
                            input_passwd.setError("密码不正确!");
                        } else if(resp.equals("email has used")){
                            TextView input_email = (TextView) findViewById(R.id.accountEdittext);
                            input_email.setError("账户已经使用!");
                        }else if(resp.equals("name has used")){
                            TextView input_name = (TextView) findViewById(R.id.usernameEdittext);
                            input_name.setError("昵称被占用!");
                        }
                    }
                }catch (Exception e){
                    TextView input_passwd = (TextView) findViewById(R.id.pwdEdittext);
                    input_passwd.setError("服务器出错!!");
                }
            }
        }
    }

}

/**
 * Created by dai on 2015/12/1.
 * 注册界面的fragment
 */
class RegisterFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.register_fragment,container,false);
    }
}


/**
 * Created by dai on 2015/12/1.
 * 登录界面的fragment
 */
class LoginFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.login_fragment,container,false);
    }
}


