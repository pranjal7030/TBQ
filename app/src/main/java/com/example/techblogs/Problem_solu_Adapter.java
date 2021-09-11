package com.example.techblogs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

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

public class Problem_solu_Adapter extends RecyclerView.Adapter<Problem_solu_Adapter.ImageViewHolder> implements Filterable {


        private List<QuestionData> uploads;
        private List<QuestionData> exampleListFull;
        private Context mContext;
        private boolean mProcessLike= false;
        private FirebaseAuth firebaseAuth;
        private ActivityFunctions activityFunctions = new ActivityFunctions(mContext);


        public Problem_solu_Adapter( Context context,List<QuestionData> uploads) {
            mContext = context;
            this.uploads = uploads;
            exampleListFull = new ArrayList<>(uploads);
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_prob_solu_list, parent, false);
            return new Problem_solu_Adapter.ImageViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ImageViewHolder holder, int position) {
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

            holder.giveSolu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent go=new Intent(mContext,GiveSolution.class);
                    go.putExtra("userid",uploadCurrent.getUserId());
                    go.putExtra("quesid",uploadCurrent.getQuestionId());
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
            public Button viewSolu,giveSolu;


            public ImageViewHolder(final View itemView) {
                super(itemView);

                textViewTitle = itemView.findViewById(R.id.text_view_titleSolu);
                circleImageView = itemView.findViewById(R.id.imageViewprofileimageSolu);
                textViewUserName = itemView.findViewById(R.id.textViewAllSolu);
                textViewDescription=itemView.findViewById(R.id.textViewSoluDesc);
                viewSolu=itemView.findViewById(R.id.buttonViewSolu);
                giveSolu=itemView.findViewById(R.id.buttonGiveSolu);
                firebaseAuth = FirebaseAuth.getInstance();



            }





        }


    }
