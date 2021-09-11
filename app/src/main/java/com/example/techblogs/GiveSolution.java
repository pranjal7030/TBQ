package com.example.techblogs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GiveSolution extends AppCompatActivity {

    private String UserId,QuesId;
    private FirebaseAuth firebaseAuth;
    private TextView QuestionTitle,QuestionDescription,QuestionerUserName;
    private EditText GiveSoluetion;
    private Button SaveSolution;
    private CircleImageView QuestionerImage;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_solution);

        firebaseAuth=FirebaseAuth.getInstance();

        Intent recieve=getIntent();
        UserId=recieve.getStringExtra("userid");
        QuesId=recieve.getStringExtra("quesid");

        QuestionTitle=findViewById(R.id.textViewQuestionTitle2);
        QuestionerUserName=findViewById(R.id.textViewQuestioner);
        QuestionerImage=findViewById(R.id.imageViewprofileimageQuestioner);
        QuestionDescription=findViewById(R.id.textViewQuestionDes2);
        GiveSoluetion=findViewById(R.id.editTextGiveSolu);
        SaveSolution=findViewById(R.id.buttonSaveSolution);

        SaveSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadSolution();
            }
        });
        gettingQuestionsData(); // getting questions and user data

        QuestionerUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open = new Intent(GiveSolution.this,ShowParticularUser.class);
                open.putExtra("userid",UserId);
                startActivity(open);
            }
        });

        QuestionerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open = new Intent(GiveSolution.this,ShowParticularUser.class);
                open.putExtra("userid",UserId);
                startActivity(open);
            }
        });
    }

    private void gettingQuestionsData() {
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Questions").child(UserId).child(QuesId);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if(dataSnapshot != null || dataSnapshot.hasChildren())
                {
                    QuestionData upload = dataSnapshot.getValue(QuestionData.class);

                    if(upload != null) {
                        QuestionTitle.setText(upload.getQuestionTitle());
                        QuestionDescription.setText(upload.getQuestionDescription());
                    }

                }

                else
                {
                    Toast.makeText(GiveSolution.this,"Nothing to show...Please add data",Toast.LENGTH_LONG).show();
                }



            }



            @Override
            public void onCancelled(DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());
            }
        });



        DatabaseReference databaseReferenceProfile= FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(UserId);
        databaseReferenceProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren() || dataSnapshot != null) {

                    if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                        Picasso.with(GiveSolution.this)
                                .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .into(QuestionerImage);
                    }

                    if (dataSnapshot.child("name").getValue(String.class) != null) {
                        QuestionerUserName.setText(dataSnapshot.child("name").getValue(String.class));

                    }


                }
                else {
                    Picasso.with(GiveSolution.this)
                            .load(R.drawable.ic_account_circle_black_24dp)
                            .into(QuestionerImage);
                    QuestionerUserName.setText(dataSnapshot.child("TechBlog User").getValue(String.class));
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

    private void uploadSolution()
    {
        final String solution = GiveSoluetion.getText().toString().trim();

        if (solution.isEmpty()) {
            GiveSoluetion.setError("Field can't be empty");

        }

        else if (GiveSoluetion.length() != 0) {

            final ProgressDialog dialog = ProgressDialog.show(GiveSolution.this, "",
                    "Uploading, Please wait...", true);

            DatabaseReference muploadSolution = FirebaseDatabase.getInstance().getReference("Solutions").child(QuesId).child(firebaseAuth.getCurrentUser().getUid());
            SolutionData solutionData=new SolutionData(solution,QuesId,firebaseAuth.getCurrentUser().getUid());
            muploadSolution.setValue(solutionData);
            dialog.cancel();
            startActivity(new Intent(GiveSolution.this,Main3Activity.class));
            finish();
            Toast.makeText(GiveSolution.this, "Solution Added successfully", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
