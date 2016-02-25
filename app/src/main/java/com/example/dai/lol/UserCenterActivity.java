package com.example.dai.lol;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dai.lol.myComponent.MyApplication;

/**
 * Created by dai on 2015/11/30.
 * 用户中心逻辑展示
 */
public class UserCenterActivity extends Activity{
    //声明变量以获取全局Application
    protected MyApplication app;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_center);
        findViews();
        //获取全局Application
        this.app = (MyApplication)getApplication();
        }

    /**
     * Logout 登出，去除登陆状态
     */
    private void Logout(){
        //设置is_login false
        app.setLogin(false);
        //清理当前用户
        app.clearCurrentUser();
        //清理SharedPreferences
        clearCurrentUserOnSharedPreferences();
    }

    //绑定事件
    private void findViews(){
        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //退出登录状态
                Logout();
                //将当前Activity移出栈,并重新加载MainActivity
                jumpBackToMain();
            }
        });
    }

    /**
     * 跳转回主页，带回登陆信息
     */
    private void jumpBackToMain(){
            //将当前Activity移出栈
            UserCenterActivity.this.finish();
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setClass(UserCenterActivity.this, MainActivity.class);
            startActivity(intent);
    }

    /**
     * 清空SharedPreferences信息
     */
    private void clearCurrentUserOnSharedPreferences(){
        SharedPreferences savecurrentuser = getSharedPreferences("currentUser", Context.MODE_PRIVATE);
        savecurrentuser.edit().clear().commit();
    }

}
