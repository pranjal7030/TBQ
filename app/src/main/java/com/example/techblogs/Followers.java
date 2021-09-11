package com.example.techblogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Followers extends AppCompatActivity {

    private String UserId;
    private ValueEventListener mDBListener;
    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private List<FollowData>  mUploads;
    private DatabaseReference mDatabaseRef;
    private ProgressBar progressBar;
    private TextView error;
    private FollowAdapter mAdapter;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_blogs);

        Intent get = getIntent();
        UserId = get.getStringExtra("userid");

        recyclerView = findViewById(R.id.recyclerViewAllBlogs);
        error = findViewById(R.id.textViewError);
        error.setVisibility(View.GONE);
        refresh=findViewById(R.id.refresh);
        progressBar = findViewById(R.id.progressBarAllBlogs);
        mUploads = new ArrayList<>();

        getFollowers();// followers data



        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent open=new Intent(Followers.this,Followers.class);
                open.putExtra("userid",UserId);
                finish();
                refresh.setRefreshing(false);
            }
        });
    }

    private void getFollowers() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Followers").child(UserId);
        progressBar.setVisibility(View.VISIBLE);


        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();
                if (dataSnapshot.hasChildren()) {


                    for(DataSnapshot postSavedSnapshot : dataSnapshot.getChildren())
                    {

                        FollowData upload = postSavedSnapshot.getValue(FollowData.class);
                        mUploads.add(upload);

                    }

                    mAdapter = new FollowAdapter(Followers.this, mUploads);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Followers.this));
                    recyclerView.setAdapter(mAdapter);
                    progressBar.setVisibility(View.INVISIBLE);


                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    error.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
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
        super.onBackPressed();
        finish();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

}