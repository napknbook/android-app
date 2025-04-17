package com.accelerate.napknbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Convo  {


    @SerializedName("pk")
    String pk ;
    @SerializedName("title")
    String title ;
    @SerializedName("content")
    String content ;
    @SerializedName("agent")
    Agent agent ;

    public Convo(String pk, String title, String content, Agent agent) {
        this.pk = pk ;
        this.title = title ;
        this.content = content ;
        this.agent = agent ;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
