package com.notfound.makeamericafitagain;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    //declaration
    private RecyclerView rv_meals;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference refRoot;
    DatabaseReference refUser;

    List<Meal> meals;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        refRoot = FirebaseDatabase.getInstance().getReference();
        refUser = refRoot.child(mUser.getUid());

        rv_meals = findViewById(R.id.rv_meals);

        mContext = this.getApplicationContext();


        //REFRESH BUTTON
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        //retrieve user's meals
        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //init meal list
                meals = new ArrayList<>();
                //retrieve meals and append
                for(int i = 1; i < dataSnapshot.child("meals").getChildrenCount()+1; i++) {
                    //retrieve and reconstruct food list
                    List<Food> list_food = new ArrayList<>();
                    for (int j = 1; j < dataSnapshot.child("meals").child(String.valueOf(i)).getChildrenCount()+1; j++) {
                        String food_name = dataSnapshot.child("meals").child(String.valueOf(i)).child(Integer.toString(j)).child("name").getValue(String.class);
                        String food_calories = dataSnapshot.child("meals").child(String.valueOf(i)).child(Integer.toString(j)).child("calories").getValue(String.class);
                        Food food = new Food(food_name, food_calories);
                        list_food.add(food);
                    }
                    String image_name = dataSnapshot.child("meals").child(String.valueOf(i)).child("image_name").getValue(String.class);
                    StringBuffer sb = new StringBuffer(image_name);
                    sb.append(".jpg");
                    image_name = sb.toString();

                    Meal meal = new Meal(list_food, Integer.toString(i), image_name);
                    meals.add(meal);



                    // use a linear layout manager
                    mLayoutManager = new LinearLayoutManager(mContext);
                    rv_meals.setLayoutManager(mLayoutManager);

                    // specify an adapter (see also next example)
                    mAdapter = new CardAdapter(meals);
                    rv_meals.setAdapter(mAdapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
