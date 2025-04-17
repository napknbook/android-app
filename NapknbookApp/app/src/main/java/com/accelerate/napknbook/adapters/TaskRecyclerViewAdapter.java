package com.accelerate.napknbook.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accelerate.napknbook.R;
import com.accelerate.napknbook.database.AppDatabase;
import com.accelerate.napknbook.database.daos.TaskDao;
import com.accelerate.napknbook.edit.EditTaskActivity;
import com.accelerate.napknbook.fragments.TaskCategoryFragment;
import com.accelerate.napknbook.models.Task;
import com.accelerate.napknbook.utils.SharedPreferencesHelper;
import com.accelerate.napknbook.viewmodels.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.MyViewHolder> {

    private final Context context;
    private List<Task> tasks;
    private final String mode;
    private final String token;
    private final TaskViewModel taskViewModel;

    public static final String MODE_ACTIVE = "active";
    public static final String MODE_COMPLETED = "completed";

    private final OnTaskClickListener taskClickListener;

    public interface OnTaskClickListener {
        void onTaskClicked(Task task);
    }

    public TaskRecyclerViewAdapter(Context context, ArrayList<Task> tasks, String mode,
                                   String token, TaskViewModel taskViewModel,
                                   OnTaskClickListener taskClickListener) {
        this.context = context;
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        this.mode = mode;
        this.token = token;
        this.taskViewModel = taskViewModel;
        this.taskClickListener = taskClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_card_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> newTasks) {
        this.tasks = newTasks != null ? newTasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView taskTextView, contentTextView, dueDateTextView, completedTextView, highPriorityTextView;
        View completedPulseView, highPriorityPulseView;
        ImageView completedRaysView, highPriorityRaysView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTextView = itemView.findViewById(R.id.taskTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            dueDateTextView = itemView.findViewById(R.id.dueDateTextView);
            completedTextView = itemView.findViewById(R.id.completedTextView);
            highPriorityTextView = itemView.findViewById(R.id.highPriorityTextView);
            completedPulseView = itemView.findViewById(R.id.pulseView0);
            completedRaysView = itemView.findViewById(R.id.raysView0);
            highPriorityPulseView = itemView.findViewById(R.id.highPriorityPulseView);
            highPriorityRaysView = itemView.findViewById(R.id.highPriorityRaysView);
        }

        public void bind(Task task) {
            taskTextView.setText(task.getTitle());
            contentTextView.setText(task.getDescription());
            dueDateTextView.setText(task.getDueDate());
            completedTextView.setText(task.isCompleted() ? "✓" : "");
            highPriorityTextView.setText(task.getPriority().equals("high") ? "❗" : "❕");

            updateStrikeThrough(task.isCompleted());

            itemView.setOnClickListener(v -> {
                if (taskClickListener != null) {
                    taskClickListener.onTaskClicked(task);
                }
            });

            completedTextView.setOnClickListener(v -> {
                task.setCompleted(!task.isCompleted());
                completedTextView.setText(task.isCompleted() ? "✓" : "");
                int color = task.isCompleted()
                        ? ContextCompat.getColor(context, R.color.green)
                        : ContextCompat.getColor(context, R.color.blue);
                animatePulse(completedPulseView, completedRaysView, color);
                taskViewModel.debounceTaskUpdate(task, token);
            });

            highPriorityTextView.setOnClickListener(v -> {
                boolean isHigh = task.getPriority().equals("high");
                task.setPriority(isHigh ? "medium" : "high");
                highPriorityTextView.setText(task.getPriority().equals("high") ? "❗" : "❕");

                int color = isHigh
                        ? ContextCompat.getColor(context, R.color.white)
                        : ContextCompat.getColor(context, R.color.red);
                animatePulse(highPriorityPulseView, highPriorityRaysView, color);
                taskViewModel.debounceTaskUpdate(task, token);
            });
        }

        private void updateStrikeThrough(boolean completed) {
            int flags = completed ? Paint.STRIKE_THRU_TEXT_FLAG : 0;
            taskTextView.setPaintFlags(flags);
            contentTextView.setPaintFlags(flags);
            dueDateTextView.setPaintFlags(flags);

            int color = ContextCompat.getColor(context, R.color.black);
            taskTextView.setTextColor(color);
            contentTextView.setTextColor(color);
            dueDateTextView.setTextColor(color);
        }

        private void animatePulse(View pulseView, ImageView raysView, int color) {
            pulseView.setBackgroundTintList(ColorStateList.valueOf(color));
            raysView.setColorFilter(color, PorterDuff.Mode.SRC_IN);

            pulseView.setVisibility(View.VISIBLE);
            pulseView.setScaleX(0.1f);
            pulseView.setScaleY(0.1f);
            pulseView.setAlpha(0.8f);

            pulseView.animate()
                    .scaleX(2.0f)
                    .scaleY(2.0f)
                    .alpha(0f)
                    .setDuration(400)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> pulseView.setVisibility(View.INVISIBLE))
                    .start();

            raysView.setVisibility(View.VISIBLE);
            raysView.setScaleX(1.0f);
            raysView.setScaleY(1.0f);
            raysView.setAlpha(1.0f);

            raysView.animate()
                    .scaleX(2.8f)
                    .scaleY(2.8f)
                    .alpha(0f)
                    .setDuration(1000)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .withEndAction(() -> raysView.setVisibility(View.INVISIBLE))
                    .start();
        }
    }
}

