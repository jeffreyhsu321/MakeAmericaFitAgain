package com.notfound.makeamericafitagain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class ResultActivity extends AppCompatActivity {

    //declaration
    private RecyclerView rv_result;
    private RecyclerView.Adapter rv_Adapter;
    private RecyclerView.LayoutManager rv_LayoutManager;

    List<Food> list_foods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //init
        rv_result = findViewById(R.id.rv_result);
        rv_result.setHasFixedSize(true);
        list_foods = MainActivity.list_foods;

        // use a linear layout manager
        rv_LayoutManager = new LinearLayoutManager(this);
        rv_result.setLayoutManager(rv_LayoutManager);

        // specify an adapter (see also next example)
        rv_Adapter = new ResultAdapter(list_foods, this);
        rv_result.setAdapter(rv_Adapter);
        rv_result.setLayoutManager(new LinearLayoutManager(this));
    }
}
