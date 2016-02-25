package com.example.dai.lol.someuser;


/**
 * Created by dai on 2015/12/10.
 */
public class CurrentUser extends User{
    private String password;
    private String email;

    public void setPassword(String in){
        password = in;
    }
    public String getPassword(){
        return password;
    }
    public void setEmail(String in){
        email = in;
    }
    public String getEmail(){
        return email;
    }
}
