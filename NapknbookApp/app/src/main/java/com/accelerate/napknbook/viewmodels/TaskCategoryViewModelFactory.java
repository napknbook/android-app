package com.accelerate.napknbook.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.accelerate.napknbook.database.repositories.TaskCategoryRepository;
import com.accelerate.napknbook.database.repositories.TaskRepository;

public class TaskCategoryViewModelFactory implements ViewModelProvider.Factory {

    private final TaskCategoryRepository repository;

    public TaskCategoryViewModelFactory(TaskCategoryRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskCategoryViewModel.class)) {
            return (T) new TaskCategoryViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
