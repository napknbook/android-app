package com.accelerate.napknbook.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.accelerate.napknbook.models.Character;
import com.accelerate.napknbook.models.User;
import com.google.gson.Gson;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.util.ArrayList;

public class SharedPreferencesHelper {

    private static final String PREFERENCES_FILE = "napknbookPrefFile2";
    private static final String AUTH_TOKEN_KEY = "auth_token";
    private static final String USER_PK_KEY = "user_pk";
    private static final String MAIN_CHARACTER_NAME_KEY = "main_character_name";
    private static final String USER_KEY = "user" ;
    private static final String ACTIVE_TASK_CATEGORY_KEY = "active_task_category";
    private static final String MAIN_CHARACTER_PK_KEY = "main_character_pk";  // ✅ Add this!


    private SharedPreferences sharedPreferences;
    private Gson gson;

    // Singleton instance of SharedPreferenceHelper
    private static SharedPreferencesHelper instance;

    private SharedPreferencesHelper(Context context) {

        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREFERENCES_FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
            String asdf = e.toString();
            // Fallback to a less secure storage mechanism or handle error
        }

        gson = new Gson();
    }

    // Singleton method to get the instance of SharedPreferenceHelper
    public static synchronized SharedPreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesHelper(context.getApplicationContext());
        }
        return instance;
    }


    public void saveUserPk(String userPk) {
        sharedPreferences.edit().putString(USER_PK_KEY, userPk).apply();
    }

    public String getUserPk() {
        return sharedPreferences.getString(USER_PK_KEY, null);
    }


    public void saveUser(User user) {
        String json = gson.toJson(user);
        sharedPreferences.edit().putString(USER_KEY, json).apply();
    }

    public User getUser() {
        String json = sharedPreferences.getString(USER_KEY, null);
        return json != null ? gson.fromJson(json, User.class) : null;
    }

    public void saveActiveTaskCategoryName(String categoryName) {
        sharedPreferences.edit().putString(ACTIVE_TASK_CATEGORY_KEY, categoryName).apply();
    }

    public String getActiveTaskCategoryName() {
        return sharedPreferences.getString(ACTIVE_TASK_CATEGORY_KEY, "My Tasks");
    }



    public void setMainCharacterName(String mainCharacterName) {
        sharedPreferences.edit().putString(MAIN_CHARACTER_NAME_KEY, mainCharacterName).apply();
    }

    public String getMainCharacterName() {
        return sharedPreferences.getString(MAIN_CHARACTER_NAME_KEY, null);
    }

    public void setMainCharacterPk(String characterPk) {
        sharedPreferences.edit().putString(MAIN_CHARACTER_PK_KEY, characterPk).apply();
    }


    public String getMainCharacterPk() {
        User user = getUser();
        String mainCharacterName = getMainCharacterName();

        if (user == null || mainCharacterName == null) return null;

        ArrayList<Character> characters = user.getCharacters();
        if (characters == null) return null;

        for (Character character : characters) {
            if (character.getName().equals(mainCharacterName)) {
                return character.getPk(); // return immediately when found
            }
        }

        return null; // not found
    }






    public void saveAuthToken(String authToken) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }


    public String getAuthToken() {
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null);
    }


    public void clearTokens() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AUTH_TOKEN_KEY);
        editor.remove(MAIN_CHARACTER_NAME_KEY);
        editor.remove(USER_PK_KEY);
        editor.apply();
    }


    public void clearPreferences() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        } catch (Exception e) {
            Log.e("SharedPreferencesHelper", "Error clearing preferences", e);
        }
    }

    public boolean isSubscribed() {

        User user = getUser() ;

        return false ;

    }
}

