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

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @NonNull
    @SerializedName("pk")
    public String pk;

    @SerializedName("name")
    public String name;

    @Ignore
    @SerializedName("characters")
    public ArrayList<Character> characters;

    // ✅ Local creation timestamp
    @ColumnInfo(name = "local_created_on")
    public long localCreatedOn; // Stored as milliseconds

    // --- Default constructor (for Room)
    public User() {
        this.localCreatedOn = System.currentTimeMillis();
    }
    // ✅ Custom constructor for app use, ignored by Room
    @Ignore
    public User(String pk, String name, ArrayList<Character> characters) {
        this.pk = pk;
        this.name = name;
        this.characters = characters;
    }

    // --- Getters and Setters ---

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Character> getCharacters() {
        return characters;
    }

    public void setCharacters(ArrayList<Character> characters) {
        this.characters = characters;
    }

    public long getLocalCreatedOn() {
        return localCreatedOn;
    }

    public void setLocalCreatedOn(long localCreatedOn) {
        this.localCreatedOn = localCreatedOn;
    }

}
