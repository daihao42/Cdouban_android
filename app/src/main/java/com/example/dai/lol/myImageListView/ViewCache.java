package com.example.dai.lol.myImageListView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dai.lol.R;


/**
 * Created by dai on 2015/12/16.
 * 定义视图的占位符类
 */
public class ViewCache {
    //定义基本的视图对象
    private View baseView;
    //定义标题的文字标签对象
    private TextView textViewTitle;
    private TextView textViewDirector;
    private TextView textViewActor;
    private TextView textViewTypes;
    private TextView textViewOntime;
    private TextView textViewID;

    //定义显示的图片对象
    private ImageView imageView;

    public ViewCache(View baseView){
        this.baseView = baseView;
    }

    //得到标题文字标签对象
    public TextView getTitleTextView(){
        if(textViewTitle == null){
            textViewTitle = (TextView)baseView.findViewById(R.id.ItemTvTitle);
        }
        return textViewTitle;
    }

    //得到导演标签
    public TextView getDirectorTextView(){
        if(textViewDirector == null){
            textViewDirector = (TextView)baseView.findViewById(R.id.ItemTvDirector);
        }
        return textViewDirector;
    }

    //得到演员标签
    public TextView getActorTextView(){
        if(textViewActor == null){
            textViewActor = (TextView)baseView.findViewById(R.id.ItemTvActor);
        }
        return textViewActor;
    }

    //得到类型标签
    public TextView getTypesTextView(){
        if(textViewTypes == null){
            textViewTypes = (TextView)baseView.findViewById(R.id.ItemTvTypes);
        }
        return textViewTypes;
    }

    //得到上映时间标签
    public TextView getOntimeTextView(){
        if(textViewOntime == null){
            textViewOntime = (TextView)baseView.findViewById(R.id.ItemTvOntime);
        }
        return textViewOntime;
    }

    //得到ID标签
    public TextView getIDTextView(){
        if(textViewID == null){
            textViewID = (TextView)baseView.findViewById(R.id.movie_id);
        }
        return textViewID;
    }

    //得到图片控件对象
    public ImageView getImageView(){
        if(imageView == null){
            imageView = (ImageView)baseView.findViewById(R.id.ItemIv);
        }
        return imageView;
    }
}
