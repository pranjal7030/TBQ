package com.example.techblogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText email;
    private Button done;
    private FirebaseAuth firebaseAuth;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        email=findViewById(R.id.editTextForgotPassword);
        done=findViewById(R.id.buttonForgotPassword);
        firebaseAuth =FirebaseAuth.getInstance();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                forgotPassword();

            }
        });
    }

    private void forgotPassword() {
        String useremail=email.getText().toString().trim();
        firebaseAuth.sendPasswordResetEmail(useremail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Intent open=new Intent(ForgotPassword.this,LoginActivity.class);
                    startActivity(open);
                    finish();
                    Toast.makeText(ForgotPassword.this,"check your email to reset password",Toast.LENGTH_LONG).show();
                }

                else
                {

                    activityFunctions.error(task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent open=new Intent(ForgotPassword.this,LoginActivity.class);
        startActivity(open);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityFunctions.CheckInternetConnection();
    }
}
