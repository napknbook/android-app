package com.accelerate.napknbook.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.accelerate.napknbook.models.Task;

import java.util.List;
@Dao
public interface TaskDao {

    // 🔍 GET TASKS

    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> getAllTasksLive();

    @Query("SELECT * FROM tasks WHERE characterPk = :characterPk AND categoryPk = :categoryPk")
    LiveData<List<Task>> getTasksByCharacterAndCategory(String characterPk, String categoryPk);


    @Query("SELECT * FROM tasks WHERE categoryPk = :categoryPk")
    LiveData<List<Task>> getTasksByCategoryLive(String categoryPk);

    @Query("SELECT * FROM tasks WHERE characterPk = :characterPk")
    List<Task> getTasksForCharacterNow(String characterPk);

    @Query("SELECT * FROM tasks WHERE characterPk = :characterPk")
    LiveData<List<Task>> getLiveTasksForCharacter(String characterPk);

    // ➕ INSERT TASKS

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTasks(List<Task> tasks);

    // ✏️ UPDATE TASKS

    @Update
    void updateTask(Task task);

    // ❌ DELETE TASKS

    @Delete
    void deleteTask(Task task);

    @Query("DELETE FROM tasks WHERE pk = :taskPk")
    void deleteTask(String taskPk);

    @Query("DELETE FROM tasks WHERE categoryPk = :categoryPk AND completed = 1")
    void deleteCompletedTasks(String categoryPk);

    @Query("DELETE FROM tasks WHERE categoryPk = :categoryPk")
    void clearTasksForCategory(String categoryPk);

    @Query("DELETE FROM tasks WHERE characterPk = :characterPk")
    void clearTasksForCharacter(String characterPk);
}
