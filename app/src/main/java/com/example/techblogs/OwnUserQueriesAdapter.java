package com.example.techblogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OwnUserQueriesAdapter extends RecyclerView.Adapter<OwnUserQueriesAdapter.ImageViewHolder> implements Filterable {


    private List<QuestionData> uploads;
    private List<QuestionData> exampleListFull;
    private Context mContext;
    private ActivityFunctions activityFunctions = new ActivityFunctions(mContext);


    public OwnUserQueriesAdapter( Context context,List<QuestionData> uploads) {
        mContext = context;
        this.uploads = uploads;
        exampleListFull = new ArrayList<>(uploads);
    }

    @Override
    public OwnUserQueriesAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_queries_list, parent, false);
        return new OwnUserQueriesAdapter.ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final OwnUserQueriesAdapter.ImageViewHolder holder, int position) {
        final QuestionData uploadCurrent = uploads.get(position);
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(uploadCurrent.getUserId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()) {

                    if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                        Picasso.with(mContext)
                                .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .into(holder.circleImageView);
                    }

                    if (dataSnapshot.child("name").getValue(String.class) != null) {
                        holder.textViewUserName.setText(dataSnapshot.child("name").getValue(String.class));

                    }


                }

                else
                {
                    Picasso.with(mContext)
                            .load(R.drawable.ic_account_circle_black_24dp)
                            .into(holder.circleImageView);
                    holder.textViewUserName.setText(dataSnapshot.child("TechBlog User").getValue(String.class));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                activityFunctions.error(databaseError.getMessage());


            }
        });


        if(uploadCurrent != null) {

            holder.textViewTitle.setText(uploadCurrent.getQuestionTitle());
            holder.textViewDescription.setText(uploadCurrent.getQuestionDescription());
        }



        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent go=new Intent(mContext,ShowParticularUser.class);
                go.putExtra("userid",uploadCurrent.getUserId());
                go.putExtra("blogid",uploadCurrent.getQuestionId());
                mContext.startActivity(go);
            }
        });

        holder.textViewUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent go=new Intent(mContext,ShowParticularUser.class);
                go.putExtra("userid",uploadCurrent.getUserId());
                go.putExtra("blogid",uploadCurrent.getQuestionId());
                mContext.startActivity(go);
            }
        });

        holder.viewSolu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go=new Intent(mContext,ViewSolution.class);
                go.putExtra("userid",uploadCurrent.getUserId());
                go.putExtra("quesid",uploadCurrent.getQuestionId());
                mContext.startActivity(go);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteQuestion(uploadCurrent.getUserId(),uploadCurrent.getQuestionId());
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go=new Intent(mContext,EditQueries.class);
                go.putExtra("userid",uploadCurrent.getUserId());
                go.putExtra("quesid",uploadCurrent.getQuestionId());
                mContext.startActivity(go);

            }
        });





    }

    private void deleteQuestion(final String userID, final String QuestionId) {
        AlertDialog.Builder alt = new AlertDialog.Builder(mContext);
        alt.setMessage("Are you sure , you want to delete this query ? ")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Questions");
                        databaseReference.child(userID).child(QuestionId).removeValue();
                        DatabaseReference solutions = FirebaseDatabase.getInstance().getReference("Solutions");
                        solutions.child(QuestionId).removeValue();
                        dialog.cancel();


                    }

                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();


                    }
                });

        AlertDialog alert = alt.create();
        alert.show();
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<QuestionData> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (QuestionData item : exampleListFull) {
                    if (item.getQuestionTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            uploads.clear();
            uploads.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView circleImageView;
        public TextView textViewTitle, textViewUserName, textViewDescription;
        public QuestionData upload;
        public FirebaseAuth firebaseAuth;
        public ImageButton edit,delete;
        public Button viewSolu;


        public ImageViewHolder(final View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.text_view_titleUserQueriesList);
            circleImageView = itemView.findViewById(R.id.imageViewprofileimageUserQueriesList);
            textViewUserName = itemView.findViewById(R.id.textViewUserQueriesList);
            textViewDescription=itemView.findViewById(R.id.textViewUserQueriesListDesc);
            viewSolu=itemView.findViewById(R.id.buttonViewUserQueriesList);
            edit=itemView.findViewById(R.id.imageButtonEdit);
            delete = itemView.findViewById(R.id.imageButtonDelete);
            firebaseAuth = FirebaseAuth.getInstance();



        }





    }


}

