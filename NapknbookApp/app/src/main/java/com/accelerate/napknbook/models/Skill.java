package com.accelerate.napknbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;
@Entity(tableName = "skills")
public class Skill {

    @PrimaryKey
    @NonNull
    @SerializedName("pk")
    private String pk;

    @SerializedName("name")
    private String name;

    @SerializedName("desc")
    private String desc;

    @SerializedName("level")
    private int level;

    @SerializedName("imgUrl")
    private String imgUrl;

    @SerializedName("characterPk")
    private String characterPk;

    @Ignore
    @SerializedName("convos")
    private List<Convo> convos; // Ignored by Room, handled separately

    // Required no-arg constructor for Room
    public Skill() {}

    // Optional helper constructor
    public Skill(String pk, String name, String desc, int level, String imgUrl) {
        this.pk = pk;
        this.name = name;
        this.desc = desc;
        this.level = level;
        this.imgUrl = imgUrl;
    }

    // Getters & Setters
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCharacterPk() {
        return characterPk;
    }

    public void setCharacterPk(String characterPk) {
        this.characterPk = characterPk;
    }

    public List<Convo> getConvos() {
        return convos;
    }

    public void setConvos(List<Convo> convos) {
        this.convos = convos;
    }
}
