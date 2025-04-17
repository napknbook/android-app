package com.accelerate.napknbook.models;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Agent {

    @SerializedName("pk")
    public String pk ;
    @SerializedName("name")
    public String name ;
    @SerializedName("bio")
    public String bio ;
    @SerializedName("skills")
    public ArrayList<Skill> skills;
    @SerializedName("characterType")
    public String characterType;



    public Agent(String pk, String name, String bio, ArrayList<Skill> skills, String characterType) {
        this.pk = pk ;
        this.name = name ;
        this.bio = bio;
        this.skills = skills ;
        this.characterType = characterType ;
    }

    public String getPk() {
        return pk;
    }



    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<Skill> skills) {
        this.skills = skills;
    }



}


