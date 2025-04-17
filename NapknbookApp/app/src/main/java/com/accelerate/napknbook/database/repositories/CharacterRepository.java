package com.accelerate.napknbook.database.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.accelerate.napknbook.api.NapknbookService;
import com.accelerate.napknbook.api.RetrofitClientInstance;
import com.accelerate.napknbook.database.AppDatabase;
import com.accelerate.napknbook.database.daos.CharacterDao;
import com.accelerate.napknbook.models.Character;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CharacterRepository {

    private final CharacterDao characterDao;
    private final NapknbookService api;
    private final Executor executor;
    private final LiveData<List<Character>> allCharactersLive;

    public CharacterRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        characterDao = db.characterDao();
        api = RetrofitClientInstance.getRetrofitInstance().create(NapknbookService.class);
        executor = Executors.newSingleThreadExecutor();
        allCharactersLive = characterDao.getAllCharactersLive(); // <-- LiveData!
    }

    public LiveData<List<Character>> getAllCharactersLive() {
        return allCharactersLive;
    }

    public void syncCharactersFromServer(String token) {

        api.getCharacters("Basic " + token).enqueue(new Callback<List<Character>>() {
            @Override
            public void onResponse(Call<List<Character>> call, Response<List<Character>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Character> characters = response.body();
                    executor.execute(() -> {
                        characterDao.deleteAllCharacters(); // optional
                        characterDao.insertCharacters(characters);
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Character>> call, Throwable t) {
                Log.e("CharacterRepository", "Failed to fetch characters: " + t.getMessage());
            }
        });
    }

    public void syncIfNeeded(String token) {
        executor.execute(() -> {
            List<Character> localCharacters = characterDao.getAllCharacters();

            if (localCharacters.isEmpty()) {
                Log.d("CharacterRepository", "No characters found, syncing from server.");
                syncCharactersFromServer(token);
                return;
            }

            Character anyCharacter = localCharacters.get(0);
            long now = System.currentTimeMillis();
            long ageMillis = now - anyCharacter.getLocalCreatedOn();

            if (ageMillis > 3600000) { // 1 hour = 3600000 ms
                Log.d("CharacterRepository", "Character data is older than 1 hour, syncing from server.");
                syncCharactersFromServer(token);
            } else {
                Log.d("CharacterRepository", "Character data is fresh, no sync needed.");
            }
        });
    }


    public void insertCharacter(Character character) {
        executor.execute(() -> characterDao.insertCharacter(character));
    }


    public void createCharacter(Character character, String token) {
        api.createCharacter("Basic " + token, character).enqueue(new Callback<Character>() {
            @Override
            public void onResponse(Call<Character> call, Response<Character> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> characterDao.insertCharacter(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Character> call, Throwable t) {
                Log.e("CharacterRepository", "Failed to create character: " + t.getMessage());
            }
        });
    }

    public void updateCharacter(Character character, String token) {
        api.updateCharacter("Basic " + token, character.getPk(), character).enqueue(new Callback<Character>() {
            @Override
            public void onResponse(Call<Character> call, Response<Character> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> characterDao.updateCharacter(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Character> call, Throwable t) {
                Log.e("CharacterRepository", "Failed to update character: " + t.getMessage());
            }
        });
    }

    public void deleteCharacter(Character character, String token) {
        api.deleteCharacter("Basic " + token, character.getPk()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    executor.execute(() -> characterDao.deleteCharacter(character));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("CharacterRepository", "Failed to delete character: " + t.getMessage());
            }
        });
    }

    public List<Character> getLocalCharacters() {
        return characterDao.getAllCharacters(); // still useful for non-LiveData needs
    }
}
