package com.example.dai.lol.myImageListView;

import com.example.dai.lol.somemovie.shortMovie;

/**
 * Created by dai on 2015/12/16.
 * 定义为ListView的一个Item对象的实体类
 */
public class ListItem {
    //item图片地址
    private String imageurl;
    //item图片的名称（ID）
    private String imagename;
    //item的title
    private String title;
    //导演
    private String director;
    //类型
    private String types;
    //主演
    private String actors;
    //上映时间
    private String ontime;

    public ListItem(shortMovie m){
        this.imageurl = m.getImageUrl();
        this.imagename = m.getId();
        this.title = m.getTitle();
        this.director = limitStr(m.getDirector());
        this.types = limitStr(m.getTypes());
        this.actors = limitStr(m.getActor());
        this.ontime = limitStr(m.getOntime());
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getOntime() {
        return ontime;
    }

    public void setOntime(String ontime) {
        this.ontime = ontime;
    }

    //长度限制，超出显示...
    public String limitStr(String s){
        if(s.length() > 14){
            s = s.substring(0,14)+"...";
        }
        return s;
    }
}
