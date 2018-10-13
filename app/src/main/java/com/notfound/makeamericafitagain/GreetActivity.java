package com.notfound.makeamericafitagain;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class GreetActivity extends AppCompatActivity implements
        View.OnClickListener{

    //declarations
    //private RelativeLayout rl;
    private Button btn_Login;
    private Button btn_Signup;
    private FirebaseAuth mAuth;

    /**
     * onCreate
     * @param savedInstanceState
     *
     * Purpose:
     *      - initialize activity state, views, Firebase ref
     */
    protected void onCreate(Bundle savedInstanceState) {
        // set up activity transitions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //api high enough for transition
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Fade());
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greet);

        //init
        //rl = findViewById(R.id.rl_Login);
        mAuth = FirebaseAuth.getInstance();
        btn_Login = findViewById(R.id.btn_Login);
        btn_Signup = findViewById(R.id.btn_Signup);


        // set Listeners
        btn_Login.setOnClickListener(this);
        btn_Signup.setOnClickListener(this);
    }


    /**
     * Purpose:
     *      - Login intent to start Signup Activity
     */
    public void intentSignup(){
        Intent i_Signup = new Intent(this, SignupActivity.class);
        startActivity(i_Signup);
    }


    /**
     * Purpose:
     *      - Login intent to start Login Activity
     */
    public void intentLogin(){
        Intent i_Login = new Intent(this, LoginActivity.class);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //animated transition
            startActivity(i_Login, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            //normal transition
            startActivity(i_Login);
        }
    }


    /**
     * onClick
     * @param v (the view that was clicked on)
     *
     * Purpose:
     *      - invoke the button listeners
     */
    public void onClick(View v){
        //declaration and init
        int id = v.getId();     //id of clicked on view

        //actions
        switch(id) {

            case R.id.btn_Signup:
                intentSignup();
                break;
            case R.id.btn_Login:
                intentLogin();
                break;
            default:
                break;
        }
    }


}
