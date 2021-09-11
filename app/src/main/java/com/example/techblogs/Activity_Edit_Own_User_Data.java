package com.example.techblogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Created by hp on 2/13/2020.
 */

public class Activity_Edit_Own_User_Data extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText title, content;
    private ImageView imageView;
    private Button save;
    private ProgressBar progressBar;
    private TextView addcoverimage;
    private String userId, blogId;
    private Upload upload;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addblogs_activity);

        Intent recieve = getIntent();
        userId = recieve.getStringExtra("userid");
        blogId = recieve.getStringExtra("blogid");


        firebaseAuth = FirebaseAuth.getInstance();


        title = findViewById(R.id.editTextTitle);
        content =  findViewById(R.id.editTextContent);
        imageView =  findViewById(R.id.imageViewCoverPhoto);
        save =  findViewById(R.id.buttonSave);
        addcoverimage =findViewById(R.id.textViewAddCoverImage);
        addcoverimage.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressbar5);
        progressBar.setVisibility(View.GONE);

        getUserData(); // getting user data



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    uploadFile();

            }
        });








    }

    private void getUserData()
    {
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Blogs").child(userId).child(blogId);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {




                if (dataSnapshot.hasChildren() || dataSnapshot != null) {
                    upload = dataSnapshot.getValue(Upload.class);

                    if(upload != null) {
                        title.setText(upload.getTitle());
                        Picasso.with(Activity_Edit_Own_User_Data.this)
                                .load(upload.getImageUrl())
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .fit()
                                .centerCrop()
                                .into(imageView);
                        content.setText(upload.getContent());
                    }

                } else {
                    Toast.makeText(Activity_Edit_Own_User_Data.this, "Nothing to show...Please add data", Toast.LENGTH_LONG).show();
                }


            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());
            }
        });
    }



    private void uploadFile() {
       final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Blogs").child(firebaseAuth.getCurrentUser().getUid());

        final String Title = title.getText().toString().trim();
        final String Content = content.getText().toString().trim();

        if (Title.isEmpty()) {
            title.setError("Field can't be empty");

        } else if (Content.isEmpty()) {
            content.setError("Field can't be empty");


        } else if (content.length() != 0) {



                                Toast.makeText(Activity_Edit_Own_User_Data.this, "Upload successful", Toast.LENGTH_LONG).show();
                                mDatabaseRef.child(blogId).child("title").setValue(Title);
                                mDatabaseRef.child(blogId).child("content").setValue(Content);
                                Intent close = new Intent(Activity_Edit_Own_User_Data.this, MainActivity.class);
                                startActivity(close);
                                finish();
        }
        }



    @Override
    protected void onStart() {
        super.onStart();

        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }





}
