package com.example.techblogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
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

/**
 * Created by CL-08-22 on 9/23/2019.
 */

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder> {
    private MyListData[] listdata;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storageRef;
    private ValueEventListener mDBListener;
    private ActivityFunctions activityFunctions ;
    private String uid;
    private ProgressDialog dialog2;


    public MyListAdapter(Context context, MyListData[] listdata) {

        this.context = context;
        this.listdata = listdata;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_item_settings, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MyListData myListData; myListData = listdata[position];
        activityFunctions= new ActivityFunctions(context);
        firebaseAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance();
        holder.textView.setText(listdata[position].getDescription());
        holder.imageView.setImageResource(listdata[position].getImgId());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                if (myListData.getDescription().equals("Change Password")) {

                   changePassword();



                } else if (myListData.getDescription().equals("Edit profile") && position == 1) {


                    openEditProfileActivity();


                }
                else if (myListData.getDescription().equals("Deactivate account")) {
                     deleteUserData();


                } else if (myListData.getDescription().equals("Give Feedback")) {

                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return listdata.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView =  itemView.findViewById(R.id.imageView);
            this.textView =  itemView.findViewById(R.id.textView);
            this.relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }


    }

   private void changePassword() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        String personEmail ="";
        if(account !=null) { personEmail = account.getEmail(); }
         String no =null;
         no = firebaseAuth.getCurrentUser().getPhoneNumber();

        if ( no != null ) {
            activityFunctions.error("Ooops!! you can't chage your password , because You are authenticated via phonenumber");

        }

        else if (firebaseAuth.getCurrentUser().getEmail().equals(personEmail))
        {
            activityFunctions.error(" Sorry !! you can't change your password from this screen because you are signIn through your google account. You will only change your password directly through your google account settings");
        }

        else
        {

            firebaseAuth.sendPasswordResetEmail(firebaseAuth.getCurrentUser().getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseAuth.signOut();
                        Intent open = new Intent(context, LoginActivity.class);
                        context.startActivity(open);
                        ((Settings) context).finish();
                        Toast.makeText(context, "check your email to change password", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }



    private void deactivateaccount()
    {

                        if (firebaseAuth.getCurrentUser() != null) {
                            firebaseAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        dialog2.cancel();
                                        Intent close = new Intent(context, LoginActivity.class);
                                        context.startActivity(close);
                                        ((Settings) context).finish();
                                        Toast.makeText(context, "Account Deactivted Succefully", Toast.LENGTH_LONG).show();

                                    } else {

                                        AlertDialog.Builder alt = new AlertDialog.Builder(context);
                                        alt.setMessage(task.getException().getMessage() + "\n" + "Some network error is there ,you need to login again to deactivate your account properly")
                                                .setCancelable(true)
                                                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog2.cancel();
                                                        Intent open = new Intent(context, LoginActivity.class);
                                                        firebaseAuth.signOut();
                                                        context.startActivity(open);
                                                        ((Settings) context).finish();
                                                        Toast.makeText(context, "Now you should login again to deactivae your account properly  ", Toast.LENGTH_LONG).show();
                                                        dialog.cancel();

                                                        dialog.cancel();


                                                    }
                                                });
                                        AlertDialog alert = alt.create();
                                        alert.show();

                                    }

                                }
                            });
                        }


                    }




    private void deleteprofile() {

        final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(firebaseAuth.getCurrentUser().getUid());
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChildren() || dataSnapshot != null) {

                    final User_Profile_pics upload = dataSnapshot.getValue(User_Profile_pics.class);
                    if (upload != null)
                    {

                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);

                        if(account != null)
                        {
                            Uri personPhoto = account.getPhotoUrl();
                            if (upload.getImageUrl().equals(String.valueOf(personPhoto)))
                            {
                                mDatabaseRef.removeValue();
                                mDatabaseRef.removeEventListener(mDBListener);
                                deleteQuestion();
                            }
                        }

                        else
                        {

                            StorageReference imageRef = storageRef.getReferenceFromUrl(upload.getImageUrl());
                            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mDatabaseRef.removeValue();
                                    mDatabaseRef.removeEventListener(mDBListener);
                                    deleteQuestion();
                                }
                            });

                        }
                    }


                }

                else
                {
                    deleteQuestion();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());
            }
        });


    }

    private void deleteUserData() {
        dialog2 = ProgressDialog.show(context, "",
                "Deactivating your account. Please wait...", true);
        AlertDialog.Builder alt = new AlertDialog.Builder(context);
        alt.setMessage("To properly Deactivate your account please login again " + "\n\n"+ "Note*:- If you deactivate your account then your all data will removed permanently from the server and you will not able to retrieve it again in future")
                .setCancelable(true)
                .setPositiveButton("Deactivate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Blogs").child(firebaseAuth.getCurrentUser().getUid());
                        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("likes");
                        final DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("saves");
                        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                if (dataSnapshot.hasChildren() || dataSnapshot != null) {


                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        final Upload upload = postSnapshot.getValue(Upload.class);
                                        if (upload != null) {
                                            StorageReference imageRef = storageRef.getReferenceFromUrl(upload.getImageUrl());
                                            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mDatabaseRef.child(upload.getBlogId()).removeValue();
                                                    databaseReference1.child(upload.getBlogId()).removeValue();


                                                }
                                            });

                                        }


                                    }

                                    mDatabaseRef.removeEventListener(mDBListener);
                                    databaseReference2.child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                                    deleteprofile();



                                }

                                else {
                                    deleteprofile();

                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                activityFunctions.error(databaseError.getMessage());
                            }
                        });

                        }

                })
                .setNegativeButton("Logout", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseAuth.signOut();
                        context.startActivity(new Intent(context,LoginActivity.class));
                        ((Settings) context).finish();
                        Toast.makeText(context, "Now you should login again and deactivae your account  ", Toast.LENGTH_LONG).show();
                        dialog.cancel();


                    }
                });
        AlertDialog alert = alt.create();
        alert.show();




    }
    private void deleteQuestion() {


                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Questions").child(firebaseAuth.getCurrentUser().getUid());
                        final DatabaseReference solutions = FirebaseDatabase.getInstance().getReference("Solutions");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                               if(dataSnapshot != null || dataSnapshot.hasChildren())
                               {
                                  for(DataSnapshot roomsnapshot : dataSnapshot.getChildren())
                                  {
                                      String questionData=roomsnapshot.child("questionId").getValue(String.class);
                                      if(questionData != null)
                                      {
                                          solutions.child(questionData).removeValue();
                                          databaseReference.child(questionData).removeValue();
                                      }
                                  }
                                  deactivateaccount();
                               }
                               else
                               {
                                   deactivateaccount();
                               }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                activityFunctions.error(databaseError.getMessage());
                            }
                        });

    }

    public void openEditProfileActivity()
    {
         Intent open = new Intent(context,EditProfile.class);
         context.startActivity(open);
    }




}


