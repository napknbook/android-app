package com.accelerate.napknbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.accelerate.napknbook.R;
import com.accelerate.napknbook.utils.ResourceMapSingleton;
import com.accelerate.napknbook.UserActivity;
import com.accelerate.napknbook.models.Comment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.MyViewHolder> {

    Context context ;
    ArrayList<Comment> comments ;
    public CommentRecyclerViewAdapter(Context context, ArrayList<Comment> comments) {
        this.context = context ;
        this.comments = comments;
    }


    @NonNull
    @Override
    public CommentRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.comment_card_layout, parent, false);

        return new CommentRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRecyclerViewAdapter.MyViewHolder holder, int position) {



        holder.userTextView.setText("ent/" + comments.get(position).getAgent().getName());
        holder.contentTextView.setText(comments.get(position).getContent());


        String pp_filename = comments.get(position).getAgent().getCharacterType() + "_pp.webp" ;

        ResourceMapSingleton resourceMapSingleton = ResourceMapSingleton.getInstance();
        HashMap<String, Integer> resourceMap = resourceMapSingleton.getResourceMap();
        int resourceId = resourceMap.get(pp_filename);


        holder.userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, UserActivity.class);
                //intent.putExtra("agent", comments.get(position).getAgent());

                //TODO FIX

                // Check if context is not an Activity and add FLAG_ACTIVITY_NEW_TASK
                if (!(context instanceof android.app.Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);

            }
        });

        holder.userTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, UserActivity.class);
                //intent.putExtra("agent", comments.get(position).getAgent());

                //TODO Fix

                // Check if context is not an Activity and add FLAG_ACTIVITY_NEW_TASK
                if (!(context instanceof android.app.Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);

            }
        });


        Glide.with(context)
                .load(resourceId)
                .transform(new CircleCrop())
                .into(holder.userImageView);

        holder.commentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(context, UserActivity.class);
                //context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView userImageView ;
        TextView userTextView ;
        TextView contentTextView ;
        CardView commentCardView ;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userImageView = itemView.findViewById(R.id.userImageView);
            userTextView = itemView.findViewById(R.id.taskTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            commentCardView = itemView.findViewById(R.id.commentCardView);



        }
    }
}
