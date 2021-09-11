package com.example.techblogs;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ActivityFunctions extends AppCompatActivity {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Context context;

    public ActivityFunctions(Context context)
    {
        this.context=context;
    }


    public void CheckInternetConnection()
    {
        CheckConnection ch = new CheckConnection();
        boolean status = ch.isNetworkAvailable(context);

        if (status) {

            //do Async task

        } else {
            // show error
            ActivityFunctions activityFunctions = new ActivityFunctions(context);
            activityFunctions.error("No Internet Connection is there , Please Ckeck your Internet Connection");
        }
    }

    public void CheckFirebaseConnection()
    {
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null)
        {
            finish();
            startActivity(new Intent(context,LoginActivity.class));
        }

    }


   public void error(String message)
    {
        AlertDialog.Builder alt = new AlertDialog.Builder(context);
        AlertDialog alert = alt.create();
        alert.setMessage(message);
        alert.show();
    }
}
