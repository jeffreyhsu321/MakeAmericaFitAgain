package com.notfound.makeamericafitagain;

public class User {

    String email;
    String password;

    String name;

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public void updateUserInfo(String name){
        this.name = name;
    }

}
