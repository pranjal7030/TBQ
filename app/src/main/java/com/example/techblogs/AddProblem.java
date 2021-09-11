package com.example.techblogs;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProblem extends Fragment {

    private EditText questionTitle,questionDescription;
    private Button saveQuestion, textReco;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseRef;
    private  ActivityFunctions activityFunctions ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_add_problem, container, false);
        activityFunctions = new ActivityFunctions(getActivity());
        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();
        firebaseAuth=FirebaseAuth.getInstance();
        questionTitle=view.findViewById(R.id.editTextAddProblemTitle);
        questionDescription=view.findViewById(R.id.editTextQuestionDes);
        saveQuestion =view. findViewById(R.id.buttonSaveQuestion);
        textReco = view.findViewById(R.id.buttonProblemTextRecog);

        textReco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),TextRecognitionActivity.class));
            }
        });

        saveQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadQuestion();
            }
        });
        return view;
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

            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                    "Uploading, Please wait...", true);

            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Questions").child(firebaseAuth.getCurrentUser().getUid());
            String Questionid = mDatabaseRef.push().getKey();
            QuestionData questionData=new QuestionData(Questionid,firebaseAuth.getCurrentUser().getUid(),Title,Content);
            mDatabaseRef.child(Questionid).setValue(questionData);
            dialog.cancel();
            FragmentTransaction ft1=getFragmentManager().beginTransaction();
            ft1.replace(R.id.content2 ,new AddProblem());
            ft1.commit();
            Toast.makeText(getActivity(), "Question Added successfully", Toast.LENGTH_LONG).show();
        }
    }


}
