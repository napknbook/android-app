package com.accelerate.napknbook;

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

import com.accelerate.napknbook.models.Convo;
import com.accelerate.napknbook.utils.ResourceMapSingleton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.ArrayList;
import java.util.HashMap;

public class ConvoRecyclerViewAdapter extends RecyclerView.Adapter<ConvoRecyclerViewAdapter.MyViewHolder> {

    Context context ;
    ArrayList<Convo> convos;
    public ConvoRecyclerViewAdapter(Context context, ArrayList<Convo> convos) {
        this.context = context ;
        this.convos = convos;
    }


    @NonNull
    @Override
    public ConvoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.conversation_card_layout, parent, false);

        return new ConvoRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConvoRecyclerViewAdapter.MyViewHolder holder, int position) {




        holder.skillTextView.setText("");
        holder.userTextView.setText("ent/" + convos.get(position).getAgent().getName());
        holder.conversationTitleTextView.setText(convos.get(position).getTitle().replaceAll("^\"|\"$", ""));
        holder.conversationContentTextView.setText(convos.get(position).getContent().replaceAll("^\"|\"$", ""));

        holder.userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, UserActivity.class);
                //TODO Fix
                //intent.putExtra("agent", convos.get(position).getAgent());

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
//                /intent.putExtra("agent", convos.get(position).getAgent());


                //TODO fix

                // Check if context is not an Activity and add FLAG_ACTIVITY_NEW_TASK
                if (!(context instanceof android.app.Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);

            }
        });

        holder.convoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ConvoActivity.class);
                //intent.putExtra("convo", convos.get(position));

                //TODO Fix 
                // Check if context is not an Activity and add FLAG_ACTIVITY_NEW_TASK
                if (!(context instanceof android.app.Activity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                context.startActivity(intent);
            }
        });


        String pp_filename = convos.get(position).getAgent().getCharacterType() + "_pp.webp" ;

        ResourceMapSingleton resourceMapSingleton = ResourceMapSingleton.getInstance();
        HashMap<String, Integer> resourceMap = resourceMapSingleton.getResourceMap();
        int resourceId = resourceMap.get(pp_filename);

        Glide.with(context)
                .load(resourceId)
                .transform(new CircleCrop())
                .into(holder.userImageView);


        Glide.with(context)
                .load(R.raw.penguin1_pp)
                .transform(new CircleCrop())
                .into(holder.skillImageView);



    }

    @Override
    public int getItemCount() {
        return convos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView skillImageView ;
        TextView skillTextView ;
        ImageView userImageView ;
        TextView userTextView ;
        TextView conversationTitleTextView ;
        TextView conversationContentTextView;
        CardView convoCardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            skillImageView = itemView.findViewById(R.id.skillImageView);
            skillTextView = itemView.findViewById(R.id.skillTextView);
            userImageView = itemView.findViewById(R.id.userImageView);
            userTextView = itemView.findViewById(R.id.taskTextView);
            conversationTitleTextView = itemView.findViewById(R.id.conversationTitleTextView);
            conversationContentTextView = itemView.findViewById(R.id.conversationDescTextView);
            convoCardView = itemView.findViewById(R.id.convoCardView);


        }
    }
}
