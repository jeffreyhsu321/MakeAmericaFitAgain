package com.notfound.makeamericafitagain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.*;

public class ResultActivity extends AppCompatActivity {

    //declaration
    private RecyclerView rv_result;
    private RecyclerView.Adapter rv_Adapter;
    private RecyclerView.LayoutManager rv_LayoutManager;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    StorageReference storageRef;
    DatabaseReference refRoot;
    DatabaseReference refUser;

    ImageView iv_food;
    TextView tv_calorie;

    List<Food> list_foods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //init
        rv_result = findViewById(R.id.rv_result);
        rv_result.setHasFixedSize(true);
        list_foods = MainActivity.list_foods;
        iv_food = findViewById(R.id.iv_food);
        tv_calorie = findViewById(R.id.tv_calorie);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        refRoot = FirebaseDatabase.getInstance().getReference();
        refUser = refRoot.child(mUser.getUid());
        storageRef = getInstance().getReference();

        //set image
        iv_food.setImageBitmap(MainActivity.imageBitmap);




        // use a linear layout manager
        rv_LayoutManager = new LinearLayoutManager(this);
        rv_result.setLayoutManager(rv_LayoutManager);

        // specify an adapter (see also next example)
        rv_Adapter = new ResultAdapter(list_foods, this);
        rv_result.setAdapter(rv_Adapter);
        rv_result.setLayoutManager(new LinearLayoutManager(this));
    }
}
