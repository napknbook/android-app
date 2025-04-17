package com.accelerate.napknbook.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.accelerate.napknbook.models.Character;

import java.util.List;
@Dao
public interface CharacterDao {

    @Query("SELECT * FROM characters")
    List<Character> getAllCharacters();

    @Query("SELECT * FROM characters")
    LiveData<List<Character>> getAllCharactersLive();

    @Query("SELECT * FROM characters WHERE pk = :characterPk")
    Character getCharacterByPk(String characterPk);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCharacter(Character character);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCharacters(List<Character> characters);

    @Update
    void updateCharacter(Character character);

    @Delete
    void deleteCharacter(Character character);

    @Query("DELETE FROM characters")
    void deleteAllCharacters();
}
