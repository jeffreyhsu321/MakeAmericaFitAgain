package com.notfound.makeamericafitagain;;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Purpose:     To register a new user account
 */
public class SignupActivity extends AppCompatActivity implements
        View.OnClickListener{

    //declaration
    TextInputEditText et_Email;
    TextInputEditText et_Password;
    Button btn_Signup;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference refRoot;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set up activity transitions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //api high enough for transition
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            getWindow().setExitTransition(new Explode());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //init
        et_Email = findViewById(R.id.et_Email);
        et_Password = findViewById(R.id.et_Password);
        btn_Signup = findViewById(R.id.btn_Signup);

        mAuth = FirebaseAuth.getInstance();
        refRoot = FirebaseDatabase.getInstance().getReference();

        dialog = new ProgressDialog(this);

        //set listeners
        btn_Signup.setOnClickListener(this);
    }

    /**
     * Purpose:     create an authorized account in firebase
     */
    public void createUser(){

        //init
        final String email = et_Email.getText().toString();
        final String password = et_Password.getText().toString();

        //start dialog
        dialog.setMessage("Registering...");
        dialog.show();

        //check if info are valid
        if(TextUtils.isEmpty(email)){
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)){
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        } else if(password.length() < 6){
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Password must be 6 characters or longer", Toast.LENGTH_SHORT).show();
            return;
        } else {

            //attempt to create user account
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //successful creation of new account
                            if(task.isSuccessful()){
                                dialog.dismiss();

                                //toast
                                Toast.makeText(getApplicationContext(), "You have successfully joined Plates!", Toast.LENGTH_SHORT).show();

                                //init user data
                                initUserData(email, password);
                            }

                            //failed creation of new account
                            else {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Error occurred. Please contact support", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    /**
     * Purpose:     create the User object to be stored in database
     */
    public void initUserData(String email, String password){
        //init
        mUser = mAuth.getCurrentUser();

        //init user object
        User userObj = new User(email, password);

        //insert user object into database
        refRoot.child(mUser.getUid()).setValue(userObj)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //successful registration

                            //intent to landing activity
                            finish();
                            Intent i_main = new Intent(getApplicationContext(), MainActivity.class);
                            finish();
                            startActivity(i_main);

                        } else {
                            //failed to register
                            Toast.makeText(getApplicationContext(), "Error occurred. Please contact support", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onClick(View v){
        //declaration and init
        int id = v.getId();     //id of the clicked on view

        //actions
        switch(id){

            case R.id.btn_Signup:
                createUser();
                break;
            default:
                break;
        }
    }
}
