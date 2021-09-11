package com.example.techblogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView GoToSignUp, ForgotPassword;
    private EditText Username,Password;
    private Button Login,phoneSingIn;
    private GoogleSignInClient mGoogleSignInClient;
    private  String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private int RC_SIGN_IN = 1;
    private SignInButton googleSignIn;
    private  GoogleSignInOptions gso;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        GoToSignUp =findViewById(R.id.textViewSignup);
        Username =findViewById(R.id.editTextEmail);
        Password =findViewById(R.id.editTextPassword);
        Login= findViewById(R.id.buttonLogin);
        progressBar=findViewById(R.id.progressBar);
        googleSignIn=findViewById(R.id.buttonGoogleSignIn);
        phoneSingIn=findViewById(R.id.buttonPhoneSignIn);
        ForgotPassword=findViewById(R.id.textViewForgotPassword);
        mAuth=FirebaseAuth.getInstance();

        gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        GoToSignUp.setOnClickListener(this);
        Login.setOnClickListener(this);

        phoneSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent open=new Intent(LoginActivity.this,Activity_Phone.class);
                startActivity(open);
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open=new Intent(LoginActivity.this,ForgotPassword.class);
                startActivity(open);
                finish();
            }
        });
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask){
        try{

            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(LoginActivity.this,"Signed In Successfully",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e){
            Toast.makeText(LoginActivity.this,"Sign In Failed",Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    // firebase login using googleSignIn
    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {
        //check if the account is null
        if (acct != null) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        String personName = account.getDisplayName();
                        Uri personPhoto = account.getPhotoUrl();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Profile_Pics");
                        User_Profile_pics user_profile_pics=new User_Profile_pics(personName , String.valueOf(personPhoto));
                        databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(user_profile_pics);
                        Intent open=new Intent(LoginActivity.this, MainActivity.class);
                        open.putExtra("GoogleSignIn","googlesignin");
                        startActivity(open);
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();


                    }
                }
            });
        }
        else{
            Toast.makeText(LoginActivity.this, " No account is selected", Toast.LENGTH_SHORT).show();
        }
    }


  //firebase login
    public  void userLogin()
    {
        String username = Username.getText().toString().trim();
        String password = Password.getText().toString().trim();

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

        if(password.length() < 6)
        {
            Password.setError("Minimum length of password should be 6");
            Password.requestFocus();
            return;
        }

        if(password.length()>8)
        {
            Password.setError("Maximum length of password should be 8");
            Password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {

                    if(mAuth.getCurrentUser().isEmailVerified())
                    {
                        Intent it= new Intent(LoginActivity.this,MainActivity.class);
                        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(it);
                        finish();
                    }
                    else
                    {
                        activityFunctions.error("Please Verify Your email , the verification link is send to your registered email");
                    }


                }

                else
                {

                    activityFunctions.error(task.getException().getMessage());
                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() !=null)
        {

            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.buttonLogin:
                userLogin();
                break;


            case R.id.textViewSignup :

                startActivity(new Intent(this,SignUpActivity.class));
                finish();
                break;


        }

    }

}
