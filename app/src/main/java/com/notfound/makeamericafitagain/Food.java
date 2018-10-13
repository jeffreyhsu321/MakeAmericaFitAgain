package com.notfound.makeamericafitagain;

public class Food {

    //attr
    String name;
    int calories; //calories per a hundred grams

    public Food() {
        this("N/A", 0);
    }
    public Food(String name)
    {
        this(name, 0);
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
