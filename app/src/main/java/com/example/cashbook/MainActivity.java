package com.example.cashbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user=FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(user!=null){
                    startActivity(new Intent(getApplicationContext(), MainMenu.class));
                    finish();
                }else{
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        },2700);
    }
}