package com.notfound.makeamericafitagain;

public class Food {

    //attr
    String name;
    int calories;

    public Food() {
        name = "N/A";
        calories = 0;
    }

    public Food(String name, int calories){
        this.name = name;
        this.calories = calories;
    }

    public String getName(){
        return this.name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setName(String name) {
        this.name = name;
    }
}
