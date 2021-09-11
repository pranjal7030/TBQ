package com.example.techblogs;

import android.content.ContentResolver;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

public class EditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView ImageViewProfile;
    private EditText name;
    private FirebaseAuth firebaseAuth;
    private Button done;
    private ProgressBar progressBar2;
    private FirebaseStorage mStorage;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private String imageurl;
    private String nam;
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
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(firebaseAuth.getCurrentUser().getUid());

        getUserData(); // getting users data

        ImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String personName=null;
                Uri personPhoto=null;

                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(EditProfile.this);
                if(account != null) {
                    personName = account.getDisplayName();
                    personPhoto = account.getPhotoUrl();
                }

                    if(nam.equals(personName) && imageurl.equals(String.valueOf(personPhoto)))
                    {
                        activityFunctions.error(" Sorry !! you can't change your profile image and name from this screen because you are signined through your google account. You will only change your profile pic and name directly through your google account settings");
                    }


                else {

                    openFileChooser();
                }

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(EditProfile.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }


        });



    }

    private void getUserData() {
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot !=null || dataSnapshot.hasChildren()) {

                    if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {
                        imageurl = dataSnapshot.child("imageUrl").getValue(String.class) ;
                        Picasso.with(EditProfile.this)
                                .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .into(ImageViewProfile);
                    }

                    if (dataSnapshot.child("name").getValue(String.class) != null) {
                        nam= dataSnapshot.child("name").getValue(String.class) ;
                        name.setText(dataSnapshot.child("name").getValue(String.class));

                    }


                }
                else
                {
                    imageurl = null;
                    nam = null;
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
            Toast.makeText(EditProfile.this, "No photo is selected", Toast.LENGTH_LONG).show();
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




        if(name.getText().toString().trim().isEmpty())
        {
            name.setError("Name required");
            name.requestFocus();
            return;
        }


        if (mImageUri != null) {


            progressBar2.setVisibility(View.VISIBLE);
            mStorage = FirebaseStorage.getInstance();
            if(imageurl==null && nam==null)
            {
                updateProfileDetails();
            }

            else {
                StorageReference imageRef = mStorage.getReferenceFromUrl(imageurl);
                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mDatabaseRef.removeValue();
                        updateProfileDetails();

                    }
                });
            }



        } else {

            progressBar2.setVisibility(View.GONE);
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent go=new Intent(EditProfile.this,MainActivity.class);
        startActivity(go);
        finish();
    }

    private void updateProfileDetails(){
       StorageReference imageRef = mStorageRef.child(System.currentTimeMillis()
                + "." + getFileExtension(mImageUri));

        mUploadTask = imageRef.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String photoStringLink = uri.toString();
                                User_Profile_pics upload = new User_Profile_pics(name.getText().toString().trim(), photoStringLink);
                                mDatabaseRef.setValue(upload);

                                progressBar2.setVisibility(View.GONE);
                                Intent go=new Intent(EditProfile.this,MainActivity.class);
                                startActivity(go);
                                finish();
                                Toast.makeText(EditProfile.this, "Upload successful", Toast.LENGTH_LONG).show();
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

    }
}
