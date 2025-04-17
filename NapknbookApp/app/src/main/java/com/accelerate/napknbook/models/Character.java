package com.accelerate.napknbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@Entity(tableName = "characters")
public class Character {

    @PrimaryKey
    @NonNull
    @SerializedName("pk")
    private String pk;

    @SerializedName("bio")
    private String bio;

    @SerializedName("name")
    private String name;

    @SerializedName("number")
    private Integer number;

    @SerializedName("characterType")
    private String characterType;

    @SerializedName("userPk")
    private String userPk;

    @ColumnInfo(name = "local_created_on")
    private long localCreatedOn;

    @Ignore
    @SerializedName("skills")
    private ArrayList<Skill> skills;

    // --- Constructors ---

    public Character() {
        this.localCreatedOn = System.currentTimeMillis();
    }

    @Ignore
    public Character(String pk, String bio, Integer number, String name, String userPk, ArrayList<Skill> skills, String characterType) {
        this.pk = pk;
        this.bio = bio;
        this.number = number;
        this.name = name;
        this.userPk = userPk;
        this.skills = skills;
        this.characterType = characterType;
        this.localCreatedOn = System.currentTimeMillis();
    }

    // --- Getters & Setters ---

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }

    public String getUserPk() {
        return userPk;
    }

    public void setUserPk(String userPk) {
        this.userPk = userPk;
    }

    public long getLocalCreatedOn() {
        return localCreatedOn;
    }

    public void setLocalCreatedOn(long localCreatedOn) {
        this.localCreatedOn = localCreatedOn;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<Skill> skills) {
        this.skills = skills;
    }
}