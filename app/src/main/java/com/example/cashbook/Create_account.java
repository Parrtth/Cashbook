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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Create_account extends AppCompatActivity {
    Animation fadein,bottom_down;
    ProgressDialog dialog;
    ConstraintLayout login_layout;
    LinearLayout top;
    TextView txt_title,txt_login;
    CardView c;
    private EditText txt_name,txt_email,txt_password,txt_confirm_password;
    private AppCompatButton btn_create;
    private String name,email,password;
    private FirebaseAuth auth;
    private DatabaseReference database;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        fadein = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        bottom_down =AnimationUtils.loadAnimation(this,R.anim.bottom_down);
        top=findViewById(R.id.linearLayout);
        c=findViewById(R.id.cardView);
        txt_login=findViewById(R.id.txt_login);
        txt_title=findViewById(R.id.txt1);
       login_layout=findViewById(R.id.login_layout);
       txt_name=findViewById(R.id.txt_person);
       txt_email=findViewById(R.id.txt_email);
       txt_password=findViewById(R.id.txt_password);
       btn_create=findViewById(R.id.btn_create);
       dialog=new ProgressDialog(this);
       txt_confirm_password=findViewById(R.id.txt_confirm_password);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance().getReference("User");

        top.setAnimation(bottom_down);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                txt_title.setAnimation(fadein);
                c.setAnimation(fadein);
                login_layout.setAnimation(fadein);
            }
        },1000);
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name=txt_name.getText().toString();
                email=txt_email.getText().toString();
                password=txt_confirm_password.getText().toString();
                if (validInput(name,email,password)) {
                    CreateUser(email,password);

                }


            }
        });
    }
    private void CreateUser(String Email,String Password){
        dialog.setMessage("Creating Account . . .");
        dialog.setCancelable(false);
        dialog.show();
        auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()){
                    FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
                    user user=new user();
                    user.setUser_Id(u.getUid());
                    user.setUser_Name(name);
                    user.setUser_Email(email);
                    AddUser(user);
                    dialog.dismiss();
                    //Toast.makeText(Create_account.this, "Account Created", Toast.LENGTH_SHORT).show();
                   Intent i=new Intent(Create_account.this, MainMenu.class);
                   startActivity(i);
                    finish();
                }
            }
        });

    }

    /*
            validInput() method will excute where the data is not null or email was not in format.
     */
    private boolean validInput(String name,String email,String Password){

        if (TextUtils.isEmpty(email)) {
            txt_email.setError("Email is required.");
            return false;
        } else if (!isValidEmail(email)) {
            txt_email.setError("Invalid email format.");
            return false;
        }
        if (TextUtils.isEmpty(Password)) {
            txt_confirm_password.setError("Password is required.");
            return false;
        }
        if(!txt_password.getText().toString().equals(password)){
            txt_confirm_password.setError("Password Is not same ");
            return false;
        }
        if (txt_password.getText().toString().length()<=5) {
            txt_password.requestFocus();
            txt_password.setError("Password is Too short Min 6 Char.");
            return false;
        }

        if(TextUtils.isEmpty(txt_password.getText().toString())){
            txt_password.setError("Password is required.");
            return false;
        }

        if (TextUtils.isEmpty(name)) {
            txt_name.setError("Name is required.");
            return false;
        }
        return true;

    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private void AddUser(user u){
      database.push().setValue(u);
    }
}