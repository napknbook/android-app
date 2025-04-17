package com.accelerate.napknbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InventoryItem{

    @SerializedName("pk")
    String pk ;
    @SerializedName("name")
    String name ;
    @SerializedName("desc")
    String desc ;
    @SerializedName("level")
    int level ;
    @SerializedName("imgUrl")
    String imgUrl ;

    public List<Convo> getConvos() {
        return convos;
    }

    public void setConvos(List<Convo> convos) {
        this.convos = convos;
    }

    List<Convo> convos ;

    public InventoryItem(String pk, String name, String desc, int level, String imgUrl) {
        this.pk = pk ;
        this.name = name;
        this.desc = desc ;
        this.level = level;
        this.imgUrl = imgUrl;
    }

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



}
