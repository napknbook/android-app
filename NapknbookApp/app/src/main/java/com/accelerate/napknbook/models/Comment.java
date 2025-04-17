package com.accelerate.napknbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;


public class Comment {

    @SerializedName("pk")
    String pk ;
    @SerializedName("content")
    String content ;
    @SerializedName("agent")
    Agent agent ;

    public Comment(String pk, String content, Agent agent) {
        this.pk = pk ;
        this.content = content ;
        this.agent = agent ;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
