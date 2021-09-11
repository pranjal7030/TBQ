package com.example.techblogs;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    RecyclerView recyclerViewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        recyclerViewSettings=findViewById(R.id.recyclerViewSettings);
        MyListData[] myListData = new MyListData[] {
                new MyListData("Change Password", R.drawable.ic_password),
                new MyListData("Edit profile", R.drawable.ic_account_circle_black),
                new MyListData("Deactivate account", android.R.drawable.ic_delete),
                new MyListData("Give FeedBack", android.R.drawable.ic_menu_edit)
        };

        MyListAdapter adapter = new MyListAdapter(Settings.this,myListData);
        recyclerViewSettings.setHasFixedSize(true);
        recyclerViewSettings.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSettings.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent open = new Intent(Settings.this,MainActivity.class);
        startActivity(open);
    }

    @Override
    protected void onStart() {
        super.onStart();
       FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser().equals(null))
        {
            finish();
            startActivity(new Intent(Settings.this,LoginActivity.class));
        }

        CheckConnection ch = new CheckConnection();
        boolean status = ch.isNetworkAvailable(getApplicationContext());

        if (status) {

            //do Async task





        } else {
            // show error
            AlertDialog.Builder alt = new AlertDialog.Builder(Settings.this);
            AlertDialog alert = alt.create();
            alert.setMessage(" No Internet Connection is there , Please Ckeck your Internet Connection");
            alert.show();
        }


    }
}
