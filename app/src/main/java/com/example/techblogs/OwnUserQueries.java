package com.example.techblogs;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OwnUserQueries extends Fragment {

    private ActivityFunctions activityFunctions;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private CircleImageView circleImageView;
    private TextView username,error;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private List<QuestionData> mUploads;
    private OwnUserQueriesAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_user_queries, container, false);
        activityFunctions = new ActivityFunctions(getActivity());
        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();
        firebaseAuth = FirebaseAuth.getInstance();
        circleImageView = view.findViewById(R.id.imageViewprofileimageOwnUserQueries);
        username = view.findViewById(R.id.textViewOwnUserQueriesName);
        error=view.findViewById(R.id.textViewShowErrorQueries);
        progressBar = view.findViewById(R.id.progressBarOwnUserQueries);
        recyclerView = view.findViewById(R.id.recyclerViewOwnUserQueries);
        mUploads = new ArrayList<>();
        showUserProfileInfo();
        getQuestionsData();// getting questions data
        return view;
    }

    private void getQuestionsData() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Questions").child(firebaseAuth.getCurrentUser().getUid());
        progressBar.setVisibility(View.VISIBLE);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();
                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            QuestionData upload = postSnapshot.getValue(QuestionData.class);
                            mUploads.add(upload);
                            mAdapter = new OwnUserQueriesAdapter(getActivity(), mUploads);
                            mAdapter.notifyDataSetChanged();
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(mAdapter);



                    }
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
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    private void showUserProfileInfo() {
        if(firebaseAuth.getCurrentUser().getUid() != null)
        {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(firebaseAuth.getCurrentUser().getUid());
            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot !=null || dataSnapshot.hasChildren()) {

                        if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                            Picasso.with(getActivity())
                                    .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                    .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                    .into(circleImageView);
                        }

                        if (dataSnapshot.child("name").getValue(String.class) != null) {
                            username.setText(dataSnapshot.child("name").getValue(String.class));

                        }


                    }

                    else {
                        Picasso.with(getActivity())
                                .load(R.drawable.ic_account_circle_black)
                                .into(circleImageView);
                        username.setText(dataSnapshot.child("TechBlog User").getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    activityFunctions.error(databaseError.getMessage());


                }
            });

        }
        else {
            Picasso.with(getActivity())
                    .load(R.drawable.ic_account_circle_black)
                    .into(circleImageView);
            username.setText("TechBlog User");
        }
    }

}
