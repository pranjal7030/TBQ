package com.example.techblogs;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavedBlogs extends Fragment {

    private ProgressBar progressBar;
    private TextView error;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    private List<Saved> mSaved;
    private ImageAdapterSavedBlogs mAdapter;
    private DatabaseReference mDatabaseSaved;
    private ValueEventListener mDBListener;
    private SwipeRefreshLayout refresh;
    private  ActivityFunctions activityFunctions ;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_all_blogs, container, false);
        activityFunctions = new ActivityFunctions(getActivity());
        activityFunctions.CheckFirebaseConnection();
        activityFunctions.CheckInternetConnection();

        firebaseAuth=FirebaseAuth.getInstance();
        error = view.findViewById(R.id.textViewError);
        error.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBarAllBlogs);
        recyclerView= view.findViewById(R.id.recyclerViewAllBlogs);
        refresh=view.findViewById(R.id.refresh);
        mSaved = new ArrayList<>();

        getSavedBlogs(); // getting saved blogs


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction ft1=getFragmentManager().beginTransaction();
                ft1.replace(R.id.content ,new SavedBlogs());
                ft1.commit();
                refresh.setRefreshing(false);
            }
        });






        return view;
    }

    private void getSavedBlogs() {
        mDatabaseSaved= FirebaseDatabase.getInstance().getReference("saves").child(firebaseAuth.getCurrentUser().getUid());
        mDatabaseSaved.keepSynced(true);
        progressBar.setVisibility(View.VISIBLE);

        mDBListener = mDatabaseSaved.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mSaved.clear();
                if(dataSnapshot.hasChildren())
                {
                    for(DataSnapshot postSavedSnapshot : dataSnapshot.getChildren())
                    {

                        Saved saved= postSavedSnapshot.getValue(Saved.class);
                        mSaved.add(saved);

                    }

                    mAdapter = new ImageAdapterSavedBlogs(getActivity(), mSaved);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mAdapter);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    progressBar.setVisibility(View.INVISIBLE);

                }

                else
                {
                    progressBar.setVisibility(View.GONE);
                    error.setText("No saved items are there");
                    error.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseSaved.removeEventListener(mDBListener);
    }



}
