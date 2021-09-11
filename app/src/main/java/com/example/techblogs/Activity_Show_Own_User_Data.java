package com.example.techblogs;

/**
 * Created by hp on 2/19/2020.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp on 2/12/2020.
 */

public class Activity_Show_Own_User_Data extends AppCompatActivity {

    TextView title,blogContent,username;
    CircleImageView profileImage;
    ImageView coverImage;
    private FirebaseStorage mStorage;
    private String userId, blogId;
    private String ImageUrl;
    private Upload upload;
    private ImageButton share;
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
        coverImage= findViewById(R.id.imageViewOwnUserData);

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

        getuserData(); // getting user data



    }

    private void getuserData() {

        DatabaseReference databaseReferenceProfile= FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(userId);
        databaseReferenceProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren() || dataSnapshot != null) {

                    if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                        Picasso.with(Activity_Show_Own_User_Data.this)
                                .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .into(profileImage);
                    }

                    if (dataSnapshot.child("name").getValue(String.class) != null) {
                        username.setText(dataSnapshot.child("name").getValue(String.class));

                    }


                }
                else {
                    Picasso.with(Activity_Show_Own_User_Data.this)
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
                    if(upload != null)
                    {
                        title.setText(upload.getTitle());
                        Picasso.with(Activity_Show_Own_User_Data.this)
                                .load(upload.getImageUrl())
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .fit()
                                .centerCrop()
                                .into(coverImage);
                        blogContent.setText(upload.getContent());
                        ImageUrl = upload.getImageUrl();
                    }



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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_delete_Blog) {

            mStorage = FirebaseStorage.getInstance();


            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Blogs").child(userId);
            StorageReference imageRef = mStorage.getReferenceFromUrl(ImageUrl);
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    databaseReference.child(blogId).removeValue();

                }
            });


            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("likes");
            databaseReference1.child(blogId).removeValue();

            final ProgressDialog dialog = ProgressDialog.show(Activity_Show_Own_User_Data.this, "",
                    "Deleting. Please wait...", true);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.cancel();
                    finish();
                    Intent close= new Intent(Activity_Show_Own_User_Data.this,MainActivity.class);
                    startActivity(close);

                }
            },2000);





            return true;
        }

        if (id == R.id.action_EditBlog)
        {

            Intent edit= new Intent(Activity_Show_Own_User_Data.this,Activity_Edit_Own_User_Data.class);
            edit.putExtra("userid",userId);
            edit.putExtra("blogid",blogId);
            finish();
            startActivity(edit);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       // finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();

    }
}