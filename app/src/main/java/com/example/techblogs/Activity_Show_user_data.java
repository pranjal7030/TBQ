package com.example.techblogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp on 2/12/2020.
 */

public class Activity_Show_user_data extends AppCompatActivity {

    TextView title,blogContent,username;
    CircleImageView profileImage;
    ImageView coverImage;
    String userId,blogId;
    private ImageButton share;
    private Upload upload;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_own_user);

        Intent recieve = getIntent();
        userId = recieve.getStringExtra("userid");
        blogId = recieve.getStringExtra("blogid");


        title = findViewById(R.id.textViewTitleOwnUser);
        share = findViewById(R.id.buttonShareData);
        blogContent= findViewById(R.id.textViewOwnUserContent);
        username=  findViewById(R.id.textViewOwnUseruserName);
        profileImage= findViewById(R.id.imageViewprofileimageOwnUserData);
        coverImage=  findViewById(R.id.imageViewOwnUserData);


        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent show=new Intent(Activity_Show_user_data.this,ShowParticularUser.class);
                show.putExtra("userid",userId);
                show.putExtra("blogid",blogId);
                startActivity(show);

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent show=new Intent(Activity_Show_user_data.this,ShowParticularUser.class);
                show.putExtra("userid",userId);
                show.putExtra("blogid",blogId);
                startActivity(show);

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(upload.getTitle() != null && upload.getContent() != null) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = upload.getTitle() + "\n" + "\n" + upload.getContent();
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share Using"));
                }

                else
                {
                    activityFunctions.error("No data is there");
                }


            }
        });

        getUserData(); // getting user data




    }

    private void getUserData() {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren() || dataSnapshot !=null) {

                    if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                        Picasso.with(Activity_Show_user_data.this)
                                .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .into(profileImage);
                    }

                    if (dataSnapshot.child("name").getValue(String.class) != null) {
                        username.setText(dataSnapshot.child("name").getValue(String.class));

                    }

                }

                else {
                    Picasso.with(Activity_Show_user_data.this)
                            .load(R.drawable.ic_account_circle_black_24dp)
                            .into(profileImage);
                    username.setText(dataSnapshot.child("TechBlog User").getValue(String.class));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());


            }
        });



        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Blogs").child(userId).child(blogId);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if(dataSnapshot != null || dataSnapshot.hasChildren())
                {
                    upload = dataSnapshot.getValue(Upload.class);

                    if(upload != null) {
                        title.setText(upload.getTitle());
                        Picasso.with(Activity_Show_user_data.this)
                                .load(upload.getImageUrl())
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .fit()
                                .centerCrop()
                                .into(coverImage);
                        blogContent.setText(upload.getContent());
                    }

                }

                else
                {
                    Toast.makeText(Activity_Show_user_data.this,"Nothing to show...Please add data",Toast.LENGTH_LONG).show();
                }



            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();
    }
}
