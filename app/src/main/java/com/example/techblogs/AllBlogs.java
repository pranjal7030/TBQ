package com.example.techblogs;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllBlogs extends Fragment {


    private ProgressBar progressBar;
    private TextView error;
    private RecyclerView recyclerView;
    private List<Upload> mUploads;
    ImageAdapterAllBlogs mAdapter;
    private DatabaseReference mDatabaseRef;
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

        error = view.findViewById(R.id.textViewError);
        error.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progressBarAllBlogs);
        recyclerView = view.findViewById(R.id.recyclerViewAllBlogs);
        refresh=view.findViewById(R.id.refresh);
        mUploads = new ArrayList<>();


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction ft1=getFragmentManager().beginTransaction();
                ft1.replace(R.id.content ,new AllBlogs());
                ft1.commit();
                refresh.setRefreshing(false);
            }
        });

        getAllUsersData(); //Getting all users data

        return view;
    }

    private void getAllUsersData() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users_Blogs");
        progressBar.setVisibility(View.VISIBLE);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUploads.clear();
                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {


                        for (DataSnapshot roomDataSnapshot : postSnapshot.getChildren()) {
                            Upload upload = roomDataSnapshot.getValue(Upload.class);
                            mUploads.add(upload);
                            mAdapter = new ImageAdapterAllBlogs(getActivity(), mUploads);
                            mAdapter.notifyDataSetChanged();
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(mAdapter);

                        }


                    }
                    progressBar.setVisibility(View.INVISIBLE);


                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    error.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
                activityFunctions.error(databaseError.getMessage());
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.example_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mAdapter !=null) {
                    mAdapter.getFilter().filter(newText);

                }

                else
                {
                    activityFunctions.error("No item is there for search");
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
