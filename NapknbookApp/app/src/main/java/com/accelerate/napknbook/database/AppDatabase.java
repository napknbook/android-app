package com.accelerate.napknbook.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.accelerate.napknbook.database.daos.CharacterDao;
import com.accelerate.napknbook.database.daos.TaskCategoryDao;
import com.accelerate.napknbook.database.daos.TaskDao;
import com.accelerate.napknbook.database.daos.UserDao;
import com.accelerate.napknbook.models.Task;
import com.accelerate.napknbook.models.TaskCategory;
import com.accelerate.napknbook.models.Character;
import com.accelerate.napknbook.models.User;

import java.util.concurrent.Executors;

@Database(entities = {Task.class, TaskCategory.class, Character.class, User.class}, version = 10, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract TaskDao taskDao();
    public abstract TaskCategoryDao taskCategoryDao();
    public abstract CharacterDao characterDao();
    public abstract UserDao userDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "napknbook.db"
            ).fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    // 🔥 Clears ALL user data
    public void wipeAllData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            INSTANCE.clearAllTables(); // ✅ This is the correct call
        });
    }
}
