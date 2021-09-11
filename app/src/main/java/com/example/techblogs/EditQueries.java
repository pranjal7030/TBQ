package com.example.techblogs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditQueries extends AppCompatActivity {
    private String Userid,QuestionId;
    private EditText questionTitle,questionDescription;
    private Button saveQuestion, textReco;
    private DatabaseReference mDatabaseRef;
    private  ActivityFunctions activityFunctions = new ActivityFunctions(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_problem);
        Intent recieve = getIntent();
        Userid = recieve.getStringExtra("userid");
        QuestionId = recieve.getStringExtra("quesid");

        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();
        questionTitle=findViewById(R.id.editTextAddProblemTitle);
        questionDescription=findViewById(R.id.editTextQuestionDes);
        saveQuestion = findViewById(R.id.buttonSaveQuestion);
        textReco = findViewById(R.id.buttonProblemTextRecog);

        getQuestionData();

        textReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditQueries.this,TextRecognitionActivity.class));
            }
        });

        saveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadQuestion();
            }
        });
    }



    private void getQuestionData() {

       DatabaseReference mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("Questions").child(Userid).child(QuestionId);
        mDatabaseRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot != null || dataSnapshot.hasChildren())
                {
                    QuestionData questionData = dataSnapshot.getValue(QuestionData.class);
                    questionTitle.setText(questionData.getQuestionTitle());
                    questionDescription.setText(questionData.getQuestionDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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


    private void uploadQuestion()
    {

        final String Title = questionTitle.getText().toString().trim();
        final String Content = questionDescription.getText().toString().trim();

        if (Title.isEmpty()) {
            questionTitle.setError("Field can't be empty");

        } else if (Content.isEmpty()) {
            questionDescription.setError("Field can't be empty");


        }



        else if (questionDescription.length() != 0) {

            final ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Uploading, Please wait...", true);

            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Questions").child(Userid);
            QuestionData questionData=new QuestionData(QuestionId,Userid,Title,Content);
            mDatabaseRef.child(QuestionId).setValue(questionData);
            dialog.cancel();
            startActivity(new Intent(this,Main3Activity.class));
            finish();
            Toast.makeText(this, "Question Added successfully", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
