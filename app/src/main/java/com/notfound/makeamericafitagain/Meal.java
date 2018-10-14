package com.notfound.makeamericafitagain;

import java.util.List;

public class Meal {

    List<Food> foods;
    String index;

    String image_name;

    public Meal(List<Food> foods, String index, String image_name){
        this.foods = foods;
        this.index = index;
        this.image_name = image_name;
    }

    public String getIndex(){
        return index;
    }

    public String getImage_name(){
        return image_name;
    }

    public Food getFood(int index){
        return this.foods.get(index);
    }


}
