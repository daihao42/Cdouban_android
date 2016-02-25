package com.example.dai.lol.myComponent;

import android.app.Application;
import android.util.Log;

import com.example.dai.lol.someuser.CurrentUser;

/**
 * Created by dai on 2015/11/30.
 * 用于保存app必要的信息，如登录状态等，基于单件模式开发，全局只有一个Application
 */
public class MyApplication extends Application{
    private boolean Is_Login;
    private CurrentUser currentUser = new CurrentUser();

    @Override
    public void onCreate(){
        //调用父类onCreate完成基本初始化
        super.onCreate();
        //默认程序开始处于未登录状态
        setLogin(false);
    }

    /**
     * 设置登录状态
     * @param is_login 表示登录状态
     */
    public void setLogin(boolean is_login){
        this.Is_Login = is_login;
    }

    /**
     * 获取登录状态
     * @return 返回boolean
     */
    public boolean getLogin(){
        return this.Is_Login;
    }

    /**
     * 设置当前用户
     */
    public void setCurrentUser(CurrentUser c){
        currentUser = c;
    }

    /**
     * 获取当前用户
     */
    public CurrentUser getCurrentUser(){
        return currentUser;
    }

    /**
     * 清理当前用户
     */
    public void clearCurrentUser(){
        Log.d("call", "clear");
        currentUser = new CurrentUser();
    }

}
