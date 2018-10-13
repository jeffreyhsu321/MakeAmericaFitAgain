package com.notfound.makeamericafitagain;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements
        View.OnClickListener{

    //declaration
    TextInputEditText et_Name;
    Button btn_Apply;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference refRoot;
    DatabaseReference refUser;

    ProgressDialog dialog;

    User userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //init
        et_Name = findViewById(R.id.et_Name);
        btn_Apply = findViewById(R.id.btn_Apply);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        refRoot = FirebaseDatabase.getInstance().getReference();
        refUser = refRoot.child(mUser.getUid());

        dialog = new ProgressDialog(this);

        //attach listeners
        btn_Apply.setOnClickListener(this);

    }


    public void updateProfile() {
        //init
        final String name = et_Name.getText().toString();

        //field validation
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Please enter your name!", Toast.LENGTH_SHORT).show();
            return;
        } else {

            //lol another validation just to be safe
            if(name.equals("")){
                Toast.makeText(getApplicationContext(), "Please make sure all fields are entered!", Toast.LENGTH_SHORT).show();
                return;
            }

            //update profile info
            dialog.setMessage("Updating profile...");
            dialog.show();
            //init user object
            refUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userObj = new User(dataSnapshot.child("email").getValue(String.class), dataSnapshot.child("password").getValue(String.class));

                    //update user object
                    userObj.updateUserInfo(name);

                    //write user object to database
                    refUser.setValue(userObj);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), "Error occurred. Please contact support", Toast.LENGTH_SHORT).show();
                    return;
                }
            });

        }
    }


    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_Apply:

                break;
        }
    }
}
