package com.accelerate.napknbook.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@Entity(tableName = "task_categories")
public class TaskCategory {

    @PrimaryKey
    @NonNull
    @SerializedName("pk")
    private String pk;

    @SerializedName("name")
    private String name;

    @SerializedName("order")
    private Integer order;

    @SerializedName("characterPk")
    private String characterPk;

    @Ignore
    @SerializedName("tasks")
    private ArrayList<Task> tasks;

    private long localCreatedOn;

    // --- Constructors ---

    public TaskCategory() {
        this.localCreatedOn = System.currentTimeMillis();
    }

    @Ignore
    public TaskCategory(String pk, String name, ArrayList<Task> tasks) {
        this.pk = pk;
        this.name = name;
        this.tasks = tasks;
        this.localCreatedOn = System.currentTimeMillis();
    }

    // --- Getters & Setters ---

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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getCharacterPk() {
        return characterPk;
    }

    public void setCharacterPk(String characterPk) {
        this.characterPk = characterPk;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public long getLocalCreatedOn() {
        return localCreatedOn;
    }

    public void setLocalCreatedOn(long localCreatedOn) {
        this.localCreatedOn = localCreatedOn;
    }
}
