package com.example.techblogs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewSolutionAdapter extends RecyclerView.Adapter<ViewSolutionAdapter.ImageViewHolder> {

    private List<SolutionData> uploads;
    private Context mContext;
    private ActivityFunctions activityFunctions = new ActivityFunctions(mContext);


    public ViewSolutionAdapter(Context context, List<SolutionData> uploads) {
        mContext = context;
        this.uploads = uploads;
    }

    @Override
    public ViewSolutionAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_followers_list, parent, false);
        return new ViewSolutionAdapter.ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewSolutionAdapter.ImageViewHolder holder, int position) {
        final SolutionData uploadCurrent = uploads.get(position);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_Profile_Pics").child(uploadCurrent.getUserId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {

                    if (dataSnapshot.child("imageUrl").getValue(String.class) != null) {

                        Picasso.with(mContext)
                                .load(dataSnapshot.child("imageUrl").getValue(String.class))
                                .placeholder(R.drawable.ic_camera_alt_black_24dp)
                                .into(holder.circleImageView);
                    }

                    if (dataSnapshot.child("name").getValue(String.class) != null) {
                        holder.textViewUserName.setText(dataSnapshot.child("name").getValue(String.class));

                    }


                } else {
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


    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView circleImageView;
        public TextView  textViewUserName;
        public SolutionData upload;
        public FirebaseAuth firebaseAuth;


        public ImageViewHolder(final View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.imageViewprofileimageFollow);
            textViewUserName = itemView.findViewById(R.id.textViewFollowName);
            firebaseAuth = FirebaseAuth.getInstance();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    upload = uploads.get(getAdapterPosition());
                    Intent show=new Intent(mContext,ShowSolution.class);
                    show.putExtra("userid",upload.getUserId());
                    show.putExtra("quesid",upload.getQuestionId());
                    mContext.startActivity(show);
                }
            });



        }

    }
}
