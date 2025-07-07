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

    @SerializedName("early_adopter_badge_level")
    public String early_adopter_badge_level;

    @SerializedName("verified_badge_level")
    public String verified_badge_level;

    @SerializedName("balance")
    public Integer balance;

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

    public String getEarly_adopter_badge_level() {
        return early_adopter_badge_level;
    }

    public void setEarly_adopter_badge_level(String early_adopter_badge_level) {
        this.early_adopter_badge_level = early_adopter_badge_level;
    }

    public String getVerified_badge_level() {
        return verified_badge_level;
    }

    public void setVerified_badge_level(String verified_badge_level) {
        this.verified_badge_level = verified_badge_level;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public long getLocalCreatedOn() {
        return localCreatedOn;
    }

    public void setLocalCreatedOn(long localCreatedOn) {
        this.localCreatedOn = localCreatedOn;
    }

}
