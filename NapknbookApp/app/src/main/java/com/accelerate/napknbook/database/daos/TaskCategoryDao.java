package com.accelerate.napknbook.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.accelerate.napknbook.models.TaskCategory;

import java.util.List;
@Dao
public interface TaskCategoryDao {

    // Use this only if you truly want ALL categories
    //@Query("SELECT * FROM task_categories")
    //List<TaskCategory> getAllTaskCategories();


    // 🔥 FIXED: Only get categories for a specific character
    @Query("SELECT * FROM task_categories WHERE characterPk = :characterPk ORDER BY `order` ASC")
    List<TaskCategory> getAllTaskCategories(String characterPk);


    // 🔥 FIXED: Only get categories for a specific character
    @Query("SELECT * FROM task_categories WHERE characterPk = :characterPk ORDER BY `order` ASC")
    LiveData<List<TaskCategory>> getAllTaskCategoriesLive(String characterPk);

    @Query("SELECT * FROM task_categories WHERE pk = :categoryPk")
    TaskCategory getTaskCategoryByPk(String categoryPk);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTaskCategory(TaskCategory category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTaskCategories(List<TaskCategory> categories);

    @Update
    void updateTaskCategory(TaskCategory category);

    @Delete
    void deleteTaskCategory(TaskCategory category);

    @Query("DELETE FROM task_categories")
    void deleteAllTaskCategories();

    @Query("SELECT * FROM task_categories WHERE characterPk = :characterPk ORDER BY `order` ASC")
    LiveData<List<TaskCategory>> getCategoriesForCharacter(String characterPk);

    @Query("DELETE FROM task_categories WHERE characterPk = :characterPk")
    void deleteTaskCategoriesForCharacter(String characterPk);


    @Query("SELECT * FROM task_categories WHERE characterPk = :characterPk ORDER BY `order` ASC")
    List<TaskCategory> getAllTaskCategoriesNow(String characterPk);  // 👈 NEW

    @Query("SELECT * FROM task_categories WHERE pk = :categoryPk")
    LiveData<TaskCategory> getCategoryByPkLive(String categoryPk);

}


