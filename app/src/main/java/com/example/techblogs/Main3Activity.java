package com.example.techblogs;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main3Activity extends AppCompatActivity {

    private ActivityFunctions activityFunctions;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home2:
                    FragmentTransaction ft1=getFragmentManager().beginTransaction();
                    ft1.replace(R.id.content2 ,new Problems_And_Solu());
                    ft1.commit();
                    return true;
                case R.id.navigation_Add2:
                    FragmentTransaction ft2=getFragmentManager().beginTransaction();
                    ft2.replace(R.id.content2 ,new AddProblem());
                    ft2.commit();
                    return true;


                case R.id.navigation_User2:
                    FragmentTransaction ft3=getFragmentManager().beginTransaction();
                    ft3.replace(R.id.content2 ,new OwnUserQueries());
                    ft3.commit();
                    return true;
            }

            return false;
        }

    };

    @Override
    protected void onStart() {
        super.onStart();
        activityFunctions=new ActivityFunctions(this);
        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        BottomNavigationView navigation = findViewById(R.id.navigation2);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction ft1=getFragmentManager().beginTransaction();
        ft1.replace(R.id.content2 ,new Problems_And_Solu());
        ft1.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(Main3Activity.this,MainActivity.class));
    }
}
