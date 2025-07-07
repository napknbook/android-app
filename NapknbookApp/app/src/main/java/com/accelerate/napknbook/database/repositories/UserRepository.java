package com.accelerate.napknbook.database.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.database.AppDatabase;
import com.accelerate.napknbook.database.daos.UserDao;
import com.accelerate.napknbook.models.User;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserDao userDao;
    private final NapknbookService api;
    public final Executor executor;
    private final LiveData<List<User>> allUsersLive;

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
        api = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
        executor = Executors.newSingleThreadExecutor();
        allUsersLive = userDao.getAllUsersLive(); // ✅ LiveData
    }

    // ✅ LiveData version
    public LiveData<List<User>> getAllUsersLive() {
        return allUsersLive;
    }

    public void syncIfNeeded(String token, String userPk) {
        executor.execute(() -> {
            List<User> localUsers = userDao.getAllUsers();  // not LiveData — direct list

            if (localUsers.isEmpty()) {
                Log.d("UserRepository", "No users found, syncing from server.");
                syncUserFromServer(token, userPk);
                return;
            }

            User user = localUsers.get(0); // Assuming only one in production
            long now = System.currentTimeMillis();
            long userCreatedTime = user.getLocalCreatedOn();
            long ageMillis = now - userCreatedTime;

            // 1 hour in milliseconds = 60 * 60 * 1000 = 3,600,000
            if (ageMillis > 3600000) {
                Log.d("UserRepository", "User is older than 1 hour, syncing from server.");
                syncUserFromServer(token, userPk);
            } else {
                Log.d("UserRepository", "User is recent, no sync needed.");
            }
        });
    }


    public void syncUserFromServer(String token, String userPk) {


        api.getUser("Bearer " + token, userPk).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        userDao.deleteAllUsers(); // optional
                        userDao.insertUser(response.body());
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserRepository", "Failed to fetch users: " + t.getMessage());
            }
        });
    }

    public void insertUser(User user) {
        executor.execute(() -> userDao.insertUser(user));
    }

    public void createUser(User user, String token) {
        api.createEntity("Bearer " + token, user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> userDao.insertUser(response.body()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserRepository", "Failed to create user: " + t.getMessage());
            }
        });
    }

    public void updateUser(User user, String token) {
        api.updateEntity("Bearer " + token, user.getPk(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> userDao.updateUser(response.body()));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserRepository", "Failed to update user: " + t.getMessage());
            }
        });
    }

    public void deleteUser(User user, String token) {
        api.deleteEntity("Bearer " + token, user.getPk()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> userDao.deleteUser(user));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("UserRepository", "Failed to delete user: " + t.getMessage());
            }
        });
    }

    // Non-LiveData version, still valid if you need synchronous access
    public List<User> getLocalUsers() {
        return userDao.getAllUsers();
    }
}
