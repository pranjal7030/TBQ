package com.example.techblogs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

/**
 * Created by hp on 1/10/2020.
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView GoToLogIn;
    private EditText Username,Password,ConfirmPassword;
    private Button SignUp;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        mAuth= FirebaseAuth.getInstance();

        Username =  findViewById(R.id.editTextRegEmail);
        Password =  findViewById(R.id.editTextRegPassword);
        ConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        progressBar = findViewById(R.id.progressBar2);
        SignUp=  findViewById(R.id.buttonSignUp);
        GoToLogIn = findViewById(R.id.textViewGoToLogin);

        GoToLogIn.setOnClickListener(this);
        SignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.buttonSignUp:
            registerUser();
            break;

            case R.id.textViewGoToLogin:
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
        }

    }


    void registerUser()
    {



        String username = Username.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String confirmPass=ConfirmPassword.getText().toString().trim();
        if(username.isEmpty())
        {
            Username.setError("Email is required");
            Username.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches())
        {
            Username.setError("Please enter a valid email");
            Username.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            Password.setError("Password is required");
            Password.requestFocus();
            return;
        }

        if(password.length()< 6 )
        {
            Password.setError("Minimum length of password should be 6");
            Password.requestFocus();
            return;
        }

        if(password.length()> 8 )
        {
            Password.setError(" Maximum length of password should be 8");
            Password.requestFocus();
            return;
        }
        if(confirmPass.isEmpty())

        {
            ConfirmPassword.setError("Please Confirm your password");
            ConfirmPassword.requestFocus();
            return;
        }

        if(!confirmPass.equals(password) )
        {
            ConfirmPassword.setError("Password will not matched, please enter correct password");
            ConfirmPassword.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {

                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                finish();
                                startActivity(new Intent(SignUpActivity.this,ProfileImage_And_Name_Activity.class));

                            }
                            else
                            {
                                activityFunctions.error(task.getException().getMessage());

                            }

                        }
                    });

                }


                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                        activityFunctions.error("This username is already registered");

                    }

                    else
                    {
                        activityFunctions.error(task.getException().getMessage());
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
        finish();
    }
}
