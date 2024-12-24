package com.example.cashbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    Animation fadein,bottom_down;
    ProgressDialog dialog;
    private String email,password;
    ConstraintLayout register_layout;
    LinearLayout top;
    TextView txt_title,txt_register;
    CardView c;
    AppCompatButton btn_login;
    EditText txt_email,txt_password;
    TextView txt_reset_password;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        fadein = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        bottom_down =AnimationUtils.loadAnimation(this,R.anim.bottom_down);
        top=findViewById(R.id.linearLayout);
        c=findViewById(R.id.cardView);
        txt_register=findViewById(R.id.txt_register);
        txt_title=findViewById(R.id.txt1);
        dialog=new ProgressDialog(this);
        register_layout=findViewById(R.id.register_layout);
        btn_login=findViewById(R.id.login);
        txt_email=findViewById(R.id.txt_email);
        txt_password=findViewById(R.id.txt_password);
        txt_reset_password=findViewById(R.id.txt_reset_password);
        top.setAnimation(bottom_down);
        auth=FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                txt_title.setAnimation(fadein);
                c.setAnimation(fadein);
                register_layout.setAnimation(fadein);
            }
        },1000);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=txt_email.getText().toString();
                password=txt_password.getText().toString();
                if(validInput(email,password)) {
                    loginUser(email, password);
                }
            }
        });
        txt_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=txt_email.getText().toString();
                //sendEmailLink(email);
                startActivity(new Intent(getApplicationContext(),forgot_password.class));
                finish();
            }
        });
        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Create_account.class));
                finish();

            }
        });
    }
    private void loginUser(String email,String password){
        dialog.setMessage("Login . . .");
        dialog.setCancelable(false);
        dialog.show();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(Login.this, "Login", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainMenu.class));
                    finish();
                }else{
                    dialog.dismiss();
                    Toast.makeText(Login.this, "Login Failed ", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void sendEmailLink(String email){
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "Link Sent on this Email: "+email, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean validInput(String email,String Password){

        if (TextUtils.isEmpty(email)) {
            txt_email.requestFocus();
            txt_email.setError("Email is required.");
            return false;
        } else if (!isValidEmail(email)) {
            txt_email.requestFocus();
            txt_email.setError("Invalid email format.");
            return false;
        }
        if (TextUtils.isEmpty(Password)) {
            txt_password.requestFocus();
            txt_password.setError("Password is required.");
            return false;
        } else if (Password.length()<=5) {
            txt_password.requestFocus();
            txt_password.setError("Password is Too short Min 6 Char.");
            return false;
        }

        return true;

    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}