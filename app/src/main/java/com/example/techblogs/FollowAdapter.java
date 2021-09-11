package com.example.techblogs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ImageViewHolder>  {
    private List<FollowData> uploads;
    private Context mContext;
    private ActivityFunctions activityFunctions = new ActivityFunctions(mContext);

    public FollowAdapter( Context context,List<FollowData> uploads) {
        mContext = context;
        this.uploads = uploads;
    }

    @Override
    public FollowAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_followers_list, parent, false);
        return new FollowAdapter.ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FollowAdapter.ImageViewHolder holder, int position) {
        final FollowData uploadCurrent = uploads.get(position);

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



        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent go=new Intent(mContext,ShowParticularUser.class);
                go.putExtra("userid",uploadCurrent.getUserId());
                mContext.startActivity(go);
            }
        });

        holder.textViewUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent go=new Intent(mContext,ShowParticularUser.class);
                go.putExtra("userid",uploadCurrent.getUserId());
                mContext.startActivity(go);
            }
        });



    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView circleImageView;
        public TextView textViewUserName;
        private FollowData upload;


        public ImageViewHolder(final View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.imageViewprofileimageFollow);
            textViewUserName = itemView.findViewById(R.id.textViewFollowName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    upload = uploads.get(getAdapterPosition());
                    Intent show=new Intent(mContext,ShowParticularUser.class);
                    show.putExtra("userid",upload.getUserId());
                    mContext.startActivity(show);
                }
            });

        }

    }

}
