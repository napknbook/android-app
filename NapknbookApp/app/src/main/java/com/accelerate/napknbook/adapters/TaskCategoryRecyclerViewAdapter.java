package com.accelerate.napknbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.accelerate.napknbook.R;
import com.accelerate.napknbook.models.TaskCategory;

import java.util.ArrayList;
import java.util.List;

public class TaskCategoryRecyclerViewAdapter extends RecyclerView.Adapter<TaskCategoryRecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<TaskCategory> taskCategories;

    public TaskCategoryRecyclerViewAdapter(Context context, ArrayList<TaskCategory> taskCategories) {
        this.context = context;
        this.taskCategories = taskCategories;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_category_card_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TaskCategory category = taskCategories.get(position);
        String categoryName = category.getName();

        if (categoryName.equals("HIGH_PRIORITY_KEY")) {
            holder.taskCategoryTextView.setText("❗");
        } else if (categoryName.equals("ADD_CATEGORY_KEY")) {
            holder.taskCategoryTextView.setText("+ Add Category");
        } else {
            holder.taskCategoryTextView.setText(categoryName);
        }

        // Optional: add click listener
        holder.taskCategoryTextView.setOnClickListener(v -> {
            // Example: show a toast or switch category
            // Toast.makeText(context, "Selected: " + categoryName, Toast.LENGTH_SHORT).show();
            //SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(v.getContext());

            // You can call a listener or callback here if needed
        });
    }

    @Override
    public int getItemCount() {
        return taskCategories.size();
    }

    public void updateData(List<TaskCategory> newList) {
        taskCategories.clear();
        taskCategories.addAll(newList);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        Button taskCategoryTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCategoryTextView = itemView.findViewById(R.id.taskCategoryTextView);
        }
    }
}




