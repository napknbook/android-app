package com.accelerate.napknbook.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.fragment.app.FragmentActivity;

import com.accelerate.napknbook.fragments.EmptyFragment;
import com.accelerate.napknbook.fragments.HighPriorityFragment;
import com.accelerate.napknbook.fragments.TaskCategoryFragment;
import com.accelerate.napknbook.models.TaskCategory;

import java.util.List;

public class TaskCategoryPagerAdapter extends FragmentStateAdapter {
    private List<TaskCategory> categories;

    public TaskCategoryPagerAdapter(@NonNull FragmentActivity activity, List<TaskCategory> categories) {
        super(activity);
        this.categories = categories;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        TaskCategory category = categories.get(position);

        if ("ADD_CATEGORY_KEY".equals(category.getName())) {
            return new EmptyFragment(); // We’ll create this below
        } else if ("HIGH_PRIORITY_KEY".equals(category.getName())) {
            return HighPriorityFragment.newInstance();
        }
        else {
            return TaskCategoryFragment.newInstance(categories.get(position).getPk());
        }
    }


    @Override
    public int getItemCount() {
        return categories.size();
    }
}
