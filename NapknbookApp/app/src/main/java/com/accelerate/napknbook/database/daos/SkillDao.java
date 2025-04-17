package com.accelerate.napknbook.database.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.accelerate.napknbook.models.Skill;

import java.util.List;

@Dao
public interface SkillDao {
    @Query("SELECT * FROM skills WHERE characterPk = :characterPk")
    List<Skill> getSkillsByCharacter(String characterPk);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSkills(List<Skill> skills);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSkill(Skill skill);

    @Delete
    void deleteSkill(Skill skill);
}
