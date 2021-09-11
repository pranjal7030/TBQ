package com.example.techblogs;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

/**
 * Created by hp on 1/17/2020.
 */

public class AddBlogs extends Fragment {

    private FirebaseAuth firebaseAuth;
    private EditText title, content;
    private ImageView imageView;
    private Button save,textReco;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProgressBar progressBar;
    private Uri mImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;
    private String blogid;
    private  ActivityFunctions activityFunctions ;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.addblogs_activity, container, false);
        activityFunctions = new ActivityFunctions(getActivity());
        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();

        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        title = view.findViewById(R.id.editTextTitle);
        textReco = view.findViewById(R.id.buttonTextRecog);
        content =  view.findViewById(R.id.editTextContent);
        imageView =  view.findViewById(R.id.imageViewCoverPhoto);
        save = view.findViewById(R.id.buttonSave);
        progressBar= view.findViewById(R.id.progressbar5);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Blogs").child(firebaseAuth.getCurrentUser().getUid());
        blogid = mDatabaseRef.push().getKey();



        mStorageRef = FirebaseStorage.getInstance().getReference("Users_Blogs_images").child(firebaseAuth.getCurrentUser().getUid()).child(blogid);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        textReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getActivity(),TextRecognitionActivity.class));
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        return view;
    }







    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null) {
            Toast.makeText(getActivity(),"No photo is selected",Toast.LENGTH_LONG).show();
        }

        else{



            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && data != null && data.getData() != null) {
                mImageUri = data.getData();

                Picasso.with(getActivity()).load(mImageUri).into(imageView);
            }

        }



    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {


        final String Title = title.getText().toString().trim();
        final String Content = content.getText().toString().trim();

        if (Title.isEmpty()) {
            title.setError("Field can't be empty");

        } else if (Content.isEmpty()) {
            content.setError("Field can't be empty");


        }



        else if (content.length() != 0) {

            if (mImageUri != null) {

                final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                        "Uploading. Please wait...", true);

                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(mImageUri));

                mUploadTask = fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        progressBar.setProgress(0);
                                    }
                                }, 500);

                                Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                                final String uid=firebaseAuth.getCurrentUser().getUid();

                                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String photoStringLink = uri.toString();
                                        Upload upload = new Upload(blogid, Title , photoStringLink , Content,uid);
                                        mDatabaseRef.child(blogid).setValue(upload);
                                        dialog.cancel();
                                        FragmentTransaction ft1=getFragmentManager().beginTransaction();
                                        ft1.replace(R.id.content ,new AddBlogs());
                                        ft1.commit();


                                    }
                                });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                activityFunctions.error(e.getMessage());
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressBar.setProgress((int) progress);
                            }
                        });
            } else {
                Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
            }
        }

        }



}
