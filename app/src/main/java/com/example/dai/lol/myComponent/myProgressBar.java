package com.example.dai.lol.myComponent;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * Created by dai on 2015/12/13.
 */
public class myProgressBar {
    public ProgressBar progressBar;
    /**
     * 动态创建ProgressBar
     * @param a
     * @return
     */
    public myProgressBar(Activity a)
    {
        //1.找到activity根部的ViewGroup，类型都为FrameLayout。
        FrameLayout rootContainer = (FrameLayout) a.findViewById(android.R.id.content);//固定写法，返回根视图
        //2.初始化控件显示的位置
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        //3.设置控件显示位置
        ProgressBar pb = new ProgressBar(a);
        pb.setLayoutParams(lp);
        pb.setVisibility(View.GONE);//默认不显示
        //4.将控件加到根节点下
        rootContainer.addView(pb);
        progressBar = pb;
    }
}
