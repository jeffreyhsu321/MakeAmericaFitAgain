package com.notfound.makeamericafitagain;

public class Food {

    //attr
    String name;
    String calories; //calories per a hundred grams

    public Food(String name)
    {
        this.name = name;
    }

    public Food(String name, String calories){
        this.name = name;
        this.calories = calories;
    }

    public String getName(){
        return this.name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public void setName(String name) {
        this.name = name;
    }
}
