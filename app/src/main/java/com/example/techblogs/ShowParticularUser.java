package com.example.techblogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class ShowParticularUser extends AppCompatActivity {

    private TextView authUserName,totalBlogs,showError,followers1,following1;
    private CircleImageView ImageViewProfileImage;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private List<Upload> mUploads;
    private ImageAdapter mAdapter;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private Button EditProfile;
    private String UserId,BlogId;
    private ImageButton follow;
    private boolean mProcessLike= false;
    private LinearLayout following,followers;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_user);

        Intent recieve=getIntent();
        UserId = recieve.getStringExtra("userid");
        BlogId = recieve.getStringExtra("blogid");

        firebaseAuth=FirebaseAuth.getInstance();
        setLike(UserId);

        ImageViewProfileImage=  findViewById(R.id.imageViewprofileimageOwnUser);
        authUserName= findViewById(R.id.textViewOwnName);
        totalBlogs= findViewById(R.id.textViewTotalBlogs);
        followers=findViewById(R.id.layoutFollowers);
        following=findViewById(R.id.layoutFollowing);
        followers1=findViewById(R.id.textViewFollowers);
        following1=findViewById(R.id.textViewFollowing);
        showError = findViewById(R.id.textViewShowError);
        EditProfile=findViewById(R.id.buttonEditProfile);
        follow=findViewById(R.id.buttonFollow);
        progressBar = findViewById(R.id.progressBarOwnUser);
        recyclerView= findViewById(R.id.recyclerViewOwnUser);
        mUploads = new ArrayList<>();
        if(firebaseAuth.getCurrentUser().getUid().equals(UserId))
        {
            EditProfile.setVisibility(View.VISIBLE);
            follow.setVisibility(View.GONE);
        }
        else
        {
           EditProfile.setVisibility(View.GONE);
           follow.setVisibility(View.VISIBLE);
        }

        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent o = new Intent( ShowParticularUser.this,EditProfile.class);
                startActivity(o);
                finish();
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open=new Intent(ShowParticularUser.this,Followers.class);
                open.putExtra("userid",UserId);
                startActivity(open);

            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent open=new Intent(ShowParticularUser.this,Following.class);
                open.putExtra("userid",UserId);
                startActivity(open);

            }
        });

        showUserProfileInfo(); //getting Users profile information
        getUsersFollowings(); // getting User Following count
        getUsersFollowers(); // getting User Follower count
        getUserTotalBlogs(); // getting users total blogs
        getUserBlogs();// getting user blogs







        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setFollow();

            }


        });










    }

    private void setFollow() {
        mProcessLike = true;

        firebaseAuth = FirebaseAuth.getInstance();
        final DatabaseReference databaseReferenceAddFollowing=FirebaseDatabase.getInstance().getReference("Followings");
        final DatabaseReference databaseReferenceAddFollowers = FirebaseDatabase.getInstance().getReference().child("Followers");
        databaseReferenceAddFollowers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ( dataSnapshot != null) {

                    if (mProcessLike) {

                        if (dataSnapshot.child(UserId).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                            databaseReferenceAddFollowers.child(UserId).child(firebaseAuth.getCurrentUser().getUid()).child("UserId").removeValue();
                            databaseReferenceAddFollowing.child(firebaseAuth.getCurrentUser().getUid()).child(UserId).child("UserId").removeValue();

                            mProcessLike = false;

                        } else {
                            databaseReferenceAddFollowers.child(UserId).child(firebaseAuth.getCurrentUser().getUid()).child("UserId").setValue(firebaseAuth.getCurrentUser().getUid());
                            databaseReferenceAddFollowing.child(firebaseAuth.getCurrentUser().getUid()).child(UserId).child("UserId").setValue(UserId);
                            mProcessLike = false;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());

            }
        });
    }

    private void getUserBlogs() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Blogs").child(UserId);
        progressBar.setVisibility(View.VISIBLE);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();

                if(dataSnapshot.hasChildren() || dataSnapshot != null) {


                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        Upload upload = postSnapshot.getValue(Upload.class);
                        upload.setKey(postSnapshot.getKey());
                        mUploads.add(upload);

                    }

                    mAdapter = new ImageAdapter(ShowParticularUser.this, mUploads);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ShowParticularUser.this));
                    progressBar.setVisibility(View.INVISIBLE);


                }

                else
                {
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

    private void getUsersFollowers() {
        if(UserId != null) {

            DatabaseReference databaseReferenceFollowCount = FirebaseDatabase.getInstance().getReference("Followers").child(UserId);
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

    private void getUserTotalBlogs() {
        if(UserId != null ) {

            DatabaseReference databaseReferenceBlogs = FirebaseDatabase.getInstance().getReference("Users_Blogs").child(UserId);
            databaseReferenceBlogs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren() || dataSnapshot != null) {

                        totalBlogs.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    AlertDialog.Builder alt = new AlertDialog.Builder(ShowParticularUser.this);
                    AlertDialog alert = alt.create();
                    alert.setMessage(databaseError.getMessage());
                    alert.show();


                }
            });
        }
    }

    private void getUsersFollowings() {
        if(UserId != null) {

            DatabaseReference databaseReferenceFollowCount = FirebaseDatabase.getInstance().getReference("Followings").child(UserId);
            databaseReferenceFollowCount.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {

                        following1.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    AlertDialog.Builder alt = new AlertDialog.Builder(ShowParticularUser.this);
                    AlertDialog alert = alt.create();
                    alert.setMessage(databaseError.getMessage());
                    alert.show();


                }
            });
        }
    }

    private void showUserProfileInfo() {
        if(UserId != null)
        {
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(UserId);
            mDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot !=null || dataSnapshot.hasChildren()) {

                        if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                            Picasso.with(ShowParticularUser.this)
                                    .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                    .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                    .into(ImageViewProfileImage);
                        }

                        if (dataSnapshot.child("name").getValue(String.class) != null) {
                            authUserName.setText(dataSnapshot.child("name").getValue(String.class));

                        }


                    }

                    else {
                        Picasso.with(ShowParticularUser.this)
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
        else {
            Picasso.with(ShowParticularUser.this)
                    .load(R.drawable.ic_account_circle_black)
                    .into(ImageViewProfileImage);
            authUserName.setText("TechBlog User");
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();

    }

    public void setLike(final String uid)
    {

        DatabaseReference databaseReferenceFollowCheck=FirebaseDatabase.getInstance().getReference("Followers");
        databaseReferenceFollowCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.child(uid).hasChildren() && dataSnapshot != null) {

                    if (dataSnapshot.child(uid).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                        follow.setImageResource(R.drawable.ic_thumb_up_black_24dp_1);
                    } else {
                        follow.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                    }
                }
                else
                {
                    follow.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());

            }
        });



    }
}

