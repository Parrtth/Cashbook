package com.example.cashbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import java.util.List;


public class forgot_password extends AppCompatActivity {
    AppCompatButton btn_next;
    ImageView img_back;
    EditText txt_email;
    private String email;
   private FirebaseAuth auth;
   ProgressDialog dialog;


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        btn_next=findViewById(R.id.next);
        img_back=findViewById(R.id.img_back);
        txt_email=findViewById(R.id.txt_email);
        dialog=new ProgressDialog(this);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               email=txt_email.getText().toString();
               if(validInput(email)) {
                   checkEmail(email);
               }

            }
        });
    }
    /*
          checkEmail method will check the user email is registered or not
            if it will register then it will send email otherwise show error massage.
     */
    public void checkEmail(String email){
        dialog.setMessage("Sending Email . . .");
        dialog.setCancelable(false);
        dialog.show();
        auth= FirebaseAuth.getInstance();
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethods = result.getSignInMethods();

                    if (signInMethods != null && !signInMethods.isEmpty()) {
                        // Email is already registered
                        // You can handle this case, e.g., show an error message
                        auth.sendPasswordResetEmail(email);
                        dialog.dismiss();
                        Toast.makeText(forgot_password.this, "Please Check Your Email", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();

                    } else {
                        // Email is not registered
                        dialog.dismiss();
                        txt_email.requestFocus();
                        txt_email.setError("Email is not Registered");
                    }
                } else {
                    // Error occurred, handle the error
                    Exception exception = task.getException();
                    if (exception != null) {
                        // Handle the error, e.g., show an error message
                    }
                }
            }
        });

    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean validInput(String email) {

        if (TextUtils.isEmpty(email)) {
            txt_email.requestFocus();
            txt_email.setError("Email is required.");
            return false;
        } else if (!isValidEmail(email)) {
            txt_email.requestFocus();
            txt_email.setError("Invalid email format.");
            return false;
        }
        return true;
    }

}