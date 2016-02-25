package com.example.dai.lol.someuser;

/**
 * Created by dai on 2015/12/10.
 */
public class User {
    protected String name = "nill";
    protected String city = "nill";
    protected String about = "nill";
    protected String image = "nill";
    public void setName(String in){
        name = in;
    }

    public void setCity(String in){
        city = in;
    }

    public void setAbout(String in){
        about = in;
    }

    public void setImage(String in){
        image = in;
    }

    public String getName(){
        return name;
    }

    public String getCity(){
        return city;
    }

    public String getAbout(){
        return about;
    }

    //返回图片的文件名
    public String getImage(){
        String[] L = image.split("/");
        return L[L.length-1];
    }

    //获取原生数据库中的Image路径以供存储在SharedPreferences
    public String getRawImage(){
        return image;
    }

    /**
     * 获取头像的真实地址
     */
    public String getImageUrl(){
        String u =image.substring(2,image.length());
        u = "http://10.139.49.212/mysite"+u;
        return u;
    }
}


