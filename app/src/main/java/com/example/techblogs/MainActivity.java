package com.example.techblogs;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.multidex.MultiDex;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private CircleImageView ImageViewProfileImage;
    private TextView authUserName,authUserEmail;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInOptions gso;
    private ActivityFunctions activityFunctions=new ActivityFunctions(this);


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    FragmentTransaction ft1=getFragmentManager().beginTransaction();
                    ft1.replace(R.id.content ,new AllBlogs());
                    ft1.commit();
                    return true;
                case R.id.navigation_Add:
                    FragmentTransaction ft2=getFragmentManager().beginTransaction();
                    ft2.replace(R.id.content ,new AddBlogs());
                    ft2.commit();
                    return true;

                case R.id.navigation_SavedBlogs:
                    FragmentTransaction ft3=getFragmentManager().beginTransaction();
                    ft3.replace(R.id.content ,new SavedBlogs());
                    ft3.commit();
                    return true;

                case R.id.navigation_User:
                    FragmentTransaction ft4=getFragmentManager().beginTransaction();
                    ft4.replace(R.id.content ,new OwnUser());
                    ft4.commit();
                    return true;
            }

            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        ft1.replace(R.id.content, new AllBlogs());
        ft1.commit();

        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        View view = navigationView.getHeaderView(0);
        ImageViewProfileImage = view.findViewById(R.id.imageViewProfileImage);
        authUserName = view.findViewById(R.id.AuthuserName);
        authUserEmail = view.findViewById(R.id.AuthUserEmail);


        if (firebaseAuth.getCurrentUser() != null) {
            getUserProfileInfo();
            if (firebaseAuth.getCurrentUser().getPhoneNumber() != null) {
                authUserEmail.setText(firebaseAuth.getCurrentUser().getPhoneNumber());
            }
            if (firebaseAuth.getCurrentUser().getEmail() != null) {
                authUserEmail.setText(firebaseAuth.getCurrentUser().getEmail());
            }


        } else {
            Picasso.with(MainActivity.this)
                    .load(R.drawable.ic_account_circle_black_24dp)
                    .into(ImageViewProfileImage);
            authUserName.setText("TechBlog User");
        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getUserProfileInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(firebaseAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot !=null) {


                    if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                        Picasso.with(MainActivity.this)
                                .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .into(ImageViewProfileImage);

                    }

                    if (dataSnapshot.child("name").getValue(String.class) != null) {
                        authUserName.setText(dataSnapshot.child("name").getValue(String.class));

                    }



                }

                else
                {
                    Picasso.with(MainActivity.this)
                            .load(R.drawable.ic_account_circle_black_24dp)
                            .into(ImageViewProfileImage);
                    authUserName.setText("TechBlog User");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());


            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Logout) {
            firebaseAuth.signOut();
            mGoogleSignInClient.signOut();
            finish();
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            // Handle the camera action
        }  else if (id == R.id.nav_solu) {
            finish();
            Intent open=new Intent(MainActivity.this,Main3Activity.class);
            startActivity(open);

        } else if (id == R.id.nav_settings) {
            finish();
            Intent open=new Intent(MainActivity.this,Settings.class);
            startActivity(open);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
