package com.notfound.makeamericafitagain;

public class User {

    String email;
    String password;

    String name;

    String calorie_today;
    String calorie_goal;

    String meals;

    public User(String email, String password){
        this.email = email;
        this.password = password;
        this.calorie_today = "0";
        this.calorie_goal = "2000";
        this.meals = "0";
    }

    public void updateUserInfo(String name){
        this.name = name;
    }

    public void updateCalorie(int calorie){
        int total = Integer.parseInt(calorie_today);
        total += calorie;
        this.calorie_today = Integer.toString(total);
    }

}
