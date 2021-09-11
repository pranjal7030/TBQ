package com.example.techblogs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowSolution extends AppCompatActivity {

    private TextView show,SolutionerUserName;
    private String Userid,QuestionId;
    private CircleImageView imageSolutioner;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_solution);

        Intent recieve=getIntent();
        Userid=recieve.getStringExtra("userid");
        QuestionId=recieve.getStringExtra("quesid");

        show=findViewById(R.id.textViewSolution);
        SolutionerUserName=findViewById(R.id.textViewSolutioner);
        imageSolutioner=findViewById(R.id.imageViewprofileimageSolutioner);

        userProfileInfo();
        getSolutions();





        imageSolutioner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open = new Intent(ShowSolution.this,ShowParticularUser.class);
                open.putExtra("userid",Userid);
                startActivity(open);
            }
        });

        SolutionerUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open = new Intent(ShowSolution.this,ShowParticularUser.class);
                open.putExtra("userid",Userid);
                startActivity(open);

            }
        });


    }

    private void getSolutions() {
        DatabaseReference solution= FirebaseDatabase.getInstance().getReference("Solutions").child(QuestionId).child(Userid);
        solution.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren() || dataSnapshot != null) {


                    if (dataSnapshot.child("solution").getValue(String.class) != null) {
                        show.setText(dataSnapshot.child("solution").getValue(String.class));

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());


            }
        });
    }

    private void userProfileInfo() {
        DatabaseReference databaseReferenceProfile= FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(Userid);
        databaseReferenceProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren() || dataSnapshot != null) {

                    if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                        Picasso.with(ShowSolution.this)
                                .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .into(imageSolutioner);
                    }

                    if (dataSnapshot.child("name").getValue(String.class) != null) {
                        SolutionerUserName.setText(dataSnapshot.child("name").getValue(String.class));

                    }


                }
                else {
                    Picasso.with(ShowSolution.this)
                            .load(R.drawable.ic_account_circle_black_24dp)
                            .into(imageSolutioner);
                    SolutionerUserName.setText(dataSnapshot.child("TechBlog User").getValue(String.class));
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
