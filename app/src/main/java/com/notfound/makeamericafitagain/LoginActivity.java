package com.notfound.makeamericafitagain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener{

    //declaration
    Button btn_Login;
    ImageButton btn_quickLogin;
    TextInputEditText et_Email;
    TextInputEditText et_Password;
    FirebaseAuth mAuth;

    ProgressDialog dialog;

    //debug
    String email_admin = "notfound@gmail.com";
    String password_admin = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set up activity transitions
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //api high enough for transition
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Fade());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //init
        btn_Login = findViewById(R.id.btn_Login);
        btn_quickLogin = findViewById(R.id.btn_quickLogin);
        et_Email = findViewById(R.id.et_Email);
        et_Password = findViewById(R.id.et_Password);
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        //set up listeners
        btn_Login.setOnClickListener(this);
        btn_quickLogin.setOnClickListener(this);
    }

    public void loginUser(){
        //declaration
        String email = et_Email.getText().toString();
        String password = et_Password.getText().toString();

        //check if info are valid
        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        } else {
            dialog.setMessage("Getting you into bananas...");
            dialog.show();
            mAuth.signInWithEmailAndPassword(et_Email.getText().toString(), et_Password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent i_main = new Intent(getApplicationContext(), MainActivity.class);
                                dialog.dismiss();
                                finish();
                                startActivity(i_main);
                            } else {
                                Toast.makeText(getApplicationContext(), "Error occurred. Please contact support", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void quickLoginAdmin(){
        dialog.setMessage("Hacking through bananas...");
        dialog.show();
        mAuth.signInWithEmailAndPassword(email_admin, password_admin)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i_main = new Intent(getApplicationContext(), MainActivity.class);
                            dialog.dismiss();
                            finish();
                            startActivity(i_main);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error occurred. Please contact support", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onClick(View v){
        int id = v.getId();     //id of the view that was clicked on

        switch(id){

            case R.id.btn_Login:
                loginUser();
                break;

            case R.id.btn_quickLogin:
                quickLoginAdmin();
                break;

            default:
                break;

        }
    }
}
