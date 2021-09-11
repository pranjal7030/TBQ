package com.example.techblogs;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hp on 1/10/2020.
 */

public class ProfileImage_And_Name_Activity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView ImageViewProfile;
    private EditText name;
    private FirebaseAuth firebaseAuth;
    private Button done;
    private ProgressBar progressBar2;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_image_and_name_activity);




        firebaseAuth=FirebaseAuth.getInstance();

        ImageViewProfile=findViewById(R.id.imageViewprofileimage);
        name= findViewById(R.id.editTextName);
        done= findViewById(R.id.buttonDone);
        progressBar2= findViewById(R.id.progressBar4);

        mStorageRef = FirebaseStorage.getInstance().getReference("Users_Profile_Pics").child(firebaseAuth.getCurrentUser().getUid());
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Profile_Pics");




        ImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openFileChooser();

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ProfileImage_And_Name_Activity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {

                    uploadFile();
                }
            }


        });




    }

    @Override
    protected void onStart() {
        super.onStart();

        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();


        if (firebaseAuth.getCurrentUser().getPhoneNumber() != null) {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(firebaseAuth.getCurrentUser().getUid());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                            finish();
                            startActivity(new Intent(ProfileImage_And_Name_Activity.this, MainActivity.class));

                        }

                        if (dataSnapshot.child("name").getValue(String.class) != null) {
                            finish();
                            startActivity(new Intent(ProfileImage_And_Name_Activity.this, MainActivity.class));

                        }


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    activityFunctions.error(databaseError.getMessage());


                }
            });


        }

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            Toast.makeText(ProfileImage_And_Name_Activity.this, "No photo is selected", Toast.LENGTH_LONG).show();
        } else {

            if (resultCode == RESULT_OK)

                if (requestCode == PICK_IMAGE_REQUEST) {
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON) // enable image guidlines
                            .start(this);
                    mImageUri = data.getData();


                }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri(); //get image uri
                    //set image to image view
                    Picasso.with(this).load(resultUri).into(ImageViewProfile);

                }


            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void uploadFile() {


        final String displayName = name.getText().toString().trim();


        if(displayName.isEmpty())
        {
            name.setError("Name required");
            name.requestFocus();
            return;
        }


        if (mImageUri != null) {


                progressBar2.setVisibility(View.VISIBLE);
            StorageReference  fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(mImageUri));

                mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String photoStringLink = uri.toString();
                                    User_Profile_pics upload = new User_Profile_pics(displayName, photoStringLink);
                                    mDatabaseRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(upload);
                                    if (firebaseAuth.getCurrentUser().getPhoneNumber() == null) {
                                        final AlertDialog.Builder alt = new AlertDialog.Builder(ProfileImage_And_Name_Activity.this);
                                        alt.setMessage("A verification link is send on your entered email, please do verification")
                                                .setCancelable(false)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        firebaseAuth.signOut();
                                                        startActivity(new Intent(ProfileImage_And_Name_Activity.this, LoginActivity.class));
                                                        finish();


                                                    }

                                                });


                                        AlertDialog alert = alt.create();
                                        alert.show();

                                    } else {
                                        progressBar2.setVisibility(View.GONE);
                                        Intent go = new Intent(ProfileImage_And_Name_Activity.this, MainActivity.class);
                                        startActivity(go);
                                        finish();
                                        Toast.makeText(ProfileImage_And_Name_Activity.this, "Registered successfully", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar2.setVisibility(View.GONE);
                            activityFunctions.error(e.getMessage());
                        }
                    });
        } else {

            progressBar2.setVisibility(View.GONE);
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        firebaseAuth.signOut();
        startActivity(new Intent(ProfileImage_And_Name_Activity.this,LoginActivity.class));
        finish();
    }


}
