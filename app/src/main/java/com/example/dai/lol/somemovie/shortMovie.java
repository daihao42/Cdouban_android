package com.example.dai.lol.somemovie;

/**
 * Created by dai on 2015/12/20.
 * 首页电影的简短介绍
 */
public class shortMovie {
    private String id;
    private String title;
    private String director;
    private String actor;
    private String types;
    private String ontime;

    //添加默认构造器，为了解决不能JSON.parse成object的bug
    public shortMovie(){

    }

    public shortMovie(String a,String b,String c,String d,String e,String f){
        this.id = a;
        this.title = b;
        this.director = c;
        this.actor = d;
        this.types = e;
        this.ontime = f;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl(){
        return "http://10.139.49.212/mysite/douban/img/"+title+".jpg";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
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

    public String getOntime() {
        return ontime;
    }

    public void setOntime(String ontime) {
        this.ontime = ontime;
    }
}
