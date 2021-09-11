package com.example.techblogs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
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

/**
 * Created by hp on 1/31/2020.
 */

public class ImageAdapterAllBlogs extends RecyclerView.Adapter<ImageAdapterAllBlogs.ImageViewHolder> implements Filterable {


    private List<Upload> uploads;
    private List<Upload> exampleListFull;
    private Context mContext;
    private boolean mProcessLike= false;
    private FirebaseAuth firebaseAuth;
    private ActivityFunctions activityFunctions = new ActivityFunctions(mContext);


    public ImageAdapterAllBlogs( Context context,List<Upload> uploads) {
        mContext = context;
        this.uploads = uploads;
        exampleListFull = new ArrayList<>(uploads);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item_allblogs, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        final Upload uploadCurrent = uploads.get(position);
        holder.setLike(uploadCurrent.getBlogId());
        holder.setSave(uploadCurrent.getBlogId());

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

        DatabaseReference databaseReferenceSetLikes = FirebaseDatabase.getInstance().getReference().child("likes").child(uploadCurrent.getBlogId());
        databaseReferenceSetLikes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null) {

                    String likes = String.valueOf(dataSnapshot.getChildrenCount());

                    holder.likes.setText(likes + " " + "likes");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(uploadCurrent != null) {

            holder.textViewTitle.setText(uploadCurrent.getTitle());

            Picasso.with(mContext)
                    .load(uploadCurrent.getImageUrl())
                    .placeholder(R.drawable.ic_camera_alt_black_24dp)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        }

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProcessLike = true;

                firebaseAuth = FirebaseAuth.getInstance();
                final DatabaseReference databaseReferenceAddLikes = FirebaseDatabase.getInstance().getReference().child("likes");
                databaseReferenceAddLikes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if ( dataSnapshot != null) {

                            if (mProcessLike) {

                                if (dataSnapshot.child(uploadCurrent.getBlogId()).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                                    databaseReferenceAddLikes.child(uploadCurrent.getBlogId()).child(firebaseAuth.getCurrentUser().getUid()).removeValue();

                                    mProcessLike = false;

                                } else {
                                    databaseReferenceAddLikes.child(uploadCurrent.getBlogId()).child(firebaseAuth.getCurrentUser().getUid()).child("userId").setValue(firebaseAuth.getCurrentUser().getUid());
                                    databaseReferenceAddLikes.child(uploadCurrent.getBlogId()).child(firebaseAuth.getCurrentUser().getUid()).child("blogId").setValue(uploadCurrent.getBlogId());
                                    mProcessLike = false;
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        activityFunctions.error(databaseError.getMessage());

                    }
                });
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProcessLike = true;

                firebaseAuth = FirebaseAuth.getInstance();
                final DatabaseReference databaseReferenceSave = FirebaseDatabase.getInstance().getReference().child("saves");
                databaseReferenceSave.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot != null) {

                            if (mProcessLike) {

                                if (dataSnapshot.child(firebaseAuth.getCurrentUser().getUid()).hasChild(uploadCurrent.getBlogId())) {
                                    databaseReferenceSave.child(firebaseAuth.getCurrentUser().getUid()).child(uploadCurrent.getBlogId()).removeValue();

                                    mProcessLike = false;

                                } else {
                                    databaseReferenceSave.child(firebaseAuth.getCurrentUser().getUid()).child(uploadCurrent.getBlogId()).child("userId").setValue(uploadCurrent.getUserId());
                                    databaseReferenceSave.child(firebaseAuth.getCurrentUser().getUid()).child(uploadCurrent.getBlogId()).child("blogId").setValue(uploadCurrent.getBlogId());
                                    mProcessLike = false;
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        activityFunctions.error(databaseError.getMessage());

                    }
                });

            }
        });

        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent go=new Intent(mContext,ShowParticularUser.class);
                go.putExtra("userid",uploadCurrent.getUserId());
                go.putExtra("blogid",uploadCurrent.getBlogId());
                mContext.startActivity(go);
            }
        });

        holder.textViewUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent go=new Intent(mContext,ShowParticularUser.class);
                go.putExtra("userid",uploadCurrent.getUserId());
                go.putExtra("blogid",uploadCurrent.getBlogId());
                mContext.startActivity(go);
            }
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go=new Intent(mContext,ViewLikes.class);
                go.putExtra("userid",uploadCurrent.getUserId());
                go.putExtra("blogid",uploadCurrent.getBlogId());
                mContext.startActivity(go);

            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(uploadCurrent.getTitle() != null && uploadCurrent.getContent() != null) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = uploadCurrent.getTitle() + "\n" + "\n" + uploadCurrent.getContent();
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    mContext.startActivity(Intent.createChooser(sharingIntent, "Share Using"));
                }

                else
                {
                    activityFunctions.error("No data is there");
                }

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
            List<Upload> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(exampleListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Upload item : exampleListFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
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
        public TextView textViewTitle,textViewUserName,likes;
        public ImageView imageView;
        public ImageButton like,save, share;
        public Upload upload;
        public DatabaseReference databaseReferenceLikes,mDatabaseReferenceSave;
        public FirebaseAuth firebaseAuth;


        public ImageViewHolder(final View itemView) {
            super(itemView);

            textViewTitle =  itemView.findViewById(R.id.text_view_title);
            imageView =  itemView.findViewById(R.id.image_view_ownUser);
            circleImageView =  itemView.findViewById(R.id.imageViewprofileimageAllBlogs);
            textViewUserName =  itemView.findViewById(R.id.textViewAllBlogs);
            likes =  itemView.findViewById(R.id.textViewLike);
            like =  itemView.findViewById(R.id.buttonViewSolu);
            save =  itemView.findViewById(R.id.buttonGiveSolu);
            share = itemView.findViewById(R.id.buttonShare);
            databaseReferenceLikes =  FirebaseDatabase.getInstance().getReference().child("likes");
            mDatabaseReferenceSave =  FirebaseDatabase.getInstance().getReference().child("saves");
            firebaseAuth = FirebaseAuth.getInstance();
            databaseReferenceLikes.keepSynced(true);






           itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    upload = uploads.get(getAdapterPosition());
                    Intent show=new Intent(mContext,Activity_Show_user_data.class);
                    show.putExtra("userid",upload.getUserId());
                    show.putExtra("blogid",upload.getBlogId());
                    mContext.startActivity(show);
                }
            });
        }

        public void setLike(final String blog_id)
        {

            databaseReferenceLikes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    if (dataSnapshot.child(blog_id).hasChildren() && dataSnapshot != null) {

                        if (dataSnapshot.child(blog_id).hasChild(firebaseAuth.getCurrentUser().getUid())) {
                            like.setImageResource(R.drawable.ic_thumb_up_black_24dp_1);
                        } else {
                            like.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                        }
                    }
                    else
                    {
                        like.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    activityFunctions.error(databaseError.getMessage());

                }
            });


        }


        public void setSave(final String blog_id)
        {

            mDatabaseReferenceSave.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {

                        if (dataSnapshot.child(firebaseAuth.getCurrentUser().getUid()).hasChildren()) {

                            if (dataSnapshot.child(firebaseAuth.getCurrentUser().getUid()).hasChild(blog_id)) {
                                save.setImageResource(R.drawable.ic_save_black_24dp_1);
                            } else {
                                save.setImageResource(R.drawable.ic_save_black_24dp);
                            }
                        } else {
                            save.setImageResource(R.drawable.ic_save_black_24dp);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    activityFunctions.error(databaseError.getMessage());

                }
            });


        }


    }



}
