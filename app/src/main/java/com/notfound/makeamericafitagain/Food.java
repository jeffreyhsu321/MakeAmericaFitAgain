package com.notfound.makeamericafitagain;

public class Food {

    //attr
    String name;
    double calories; //calories per a hundred grams

    public Food() {
        this("N/A", 0);
    }
    public Food(String name)
    {
        this(name, 0);
    }

    public Food(String name, double calories){
        this.name = name;
        this.calories = calories;
    }

    public String getName(){
        return this.name;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setName(String name) {
        this.name = name;
    }
}
