package com.accelerate.napknbook.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.accelerate.napknbook.database.repositories.TaskRepository;
import com.accelerate.napknbook.utils.TaskUpdateDebouncer;

public class TaskViewModelFactory implements ViewModelProvider.Factory {

    private final TaskRepository repository;


    public TaskViewModelFactory(TaskRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskViewModel.class)) {
            return (T) new TaskViewModel(repository, new TaskUpdateDebouncer(repository));
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
