package com.example.techblogs;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OwnUser extends Fragment  {

    private TextView authUserName,totalBlogs,showError,following1,followers1;
    private CircleImageView ImageViewProfileImage;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private List<Upload> mUploads;
    private ImageAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private Button EditProfile;
    private ImageButton follow;
    private Upload upload;
    private LinearLayout following,followers;
    private  ActivityFunctions activityFunctions;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_own_user, container, false);
        activityFunctions = new ActivityFunctions(getActivity());
        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();

        firebaseAuth=FirebaseAuth.getInstance();


        ImageViewProfileImage=  view.findViewById(R.id.imageViewprofileimageOwnUser);
        authUserName= view.findViewById(R.id.textViewOwnName);
        totalBlogs= view.findViewById(R.id.textViewTotalBlogs);
        followers=view.findViewById(R.id.layoutFollowers);
        following=view.findViewById(R.id.layoutFollowing);
        followers1=view.findViewById(R.id.textViewFollowers);
        following1=view.findViewById(R.id.textViewFollowing);
        showError = view.findViewById(R.id.textViewShowError);
        showError.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBarOwnUser);
        EditProfile= view.findViewById(R.id.buttonEditProfile);
        follow=view.findViewById(R.id.buttonFollow);


        recyclerView= view.findViewById(R.id.recyclerViewOwnUser);


        mUploads = new ArrayList<>();


        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent o = new Intent(getActivity(),EditProfile.class);
                startActivity(o);
                getActivity().finish();
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open=new Intent(getActivity(),Followers.class);
                open.putExtra("userid",firebaseAuth.getCurrentUser().getUid());
                startActivity(open);

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent open=new Intent(getActivity(),Following.class);
                open.putExtra("userid",firebaseAuth.getCurrentUser().getUid());
                startActivity(open);

            }
        });

        showUserProfileInfo(); //getting Users profile information
        getUsersFollowings(); // getting User Following count
        getUsersFollowers(); // getting User Follower count
        getUserTotalBlogs(); // getting users total blogs
        getUserBlogs();// getting user blogs


        return view;
    }

    private void getUserBlogs() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(firebaseAuth.getCurrentUser() != null || firebaseAuth.getCurrentUser().getPhoneNumber() != null) {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Blogs").child(user.getUid());
            progressBar.setVisibility(View.VISIBLE);

            mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mUploads.clear();

                    if (dataSnapshot.hasChildren()) {


                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            upload = postSnapshot.getValue(Upload.class);
                            upload.setKey(postSnapshot.getKey());
                            mUploads.add(upload);

                        }

                        mAdapter = new ImageAdapter(getActivity(), mUploads);
                        mAdapter.notifyDataSetChanged();
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        progressBar.setVisibility(View.INVISIBLE);


                    } else {
                        progressBar.setVisibility(View.GONE);
                        showError.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.INVISIBLE);
                    activityFunctions.error(databaseError.getMessage());
                }
            });
        }

    }

    private void getUserTotalBlogs() {
        if(firebaseAuth.getCurrentUser() != null || firebaseAuth.getCurrentUser().getPhoneNumber() != null) {

            DatabaseReference databaseReferenceBlogs = FirebaseDatabase.getInstance().getReference("Users_Blogs").child(firebaseAuth.getCurrentUser().getUid());
            databaseReferenceBlogs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {

                        totalBlogs.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    activityFunctions.error(databaseError.getMessage());


                }
            });
        }

    }

    private void getUsersFollowers() {
        if (firebaseAuth.getCurrentUser().getUid() != null || firebaseAuth.getCurrentUser().getPhoneNumber() != null) {

            DatabaseReference databaseReferenceFollowCount = FirebaseDatabase.getInstance().getReference("Followers").child(firebaseAuth.getCurrentUser().getUid());
            databaseReferenceFollowCount.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {

                        followers1.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    activityFunctions.error(databaseError.getMessage());


                }
            });
        }
    }

    private void getUsersFollowings() {
        if (firebaseAuth.getCurrentUser() != null || firebaseAuth.getCurrentUser().getPhoneNumber() != null) {

            DatabaseReference databaseReferenceFollowCount = FirebaseDatabase.getInstance().getReference("Followings").child(firebaseAuth.getCurrentUser().getUid());
            databaseReferenceFollowCount.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {

                        following1.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    activityFunctions.error(databaseError.getMessage());


                }
            });
        }
    }

    private void showUserProfileInfo()
    {
        if (firebaseAuth.getCurrentUser().getUid() != null || firebaseAuth.getCurrentUser().getPhoneNumber() != null) {

            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(firebaseAuth.getCurrentUser().getUid());
            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null || dataSnapshot.hasChildren()) {

                        if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                            Picasso.with(getActivity())
                                    .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                    .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                    .into(ImageViewProfileImage);
                        }

                        if (dataSnapshot.child("name").getValue(String.class) != null) {
                            authUserName.setText(dataSnapshot.child("name").getValue(String.class));

                        }


                    } else {
                        Picasso.with(getActivity())
                                .load(R.drawable.ic_account_circle_black)
                                .into(ImageViewProfileImage);
                        authUserName.setText(dataSnapshot.child("TechBlog User").getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    activityFunctions.error(databaseError.getMessage());


                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }
}
