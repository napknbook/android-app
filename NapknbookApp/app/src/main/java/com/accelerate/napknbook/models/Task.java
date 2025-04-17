package com.accelerate.napknbook.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey
    @NonNull
    @SerializedName("pk")
    private String pk;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("created_on")
    private String createdOn;

    @SerializedName("priority")
    private String priority;

    @SerializedName("status")
    private String status;

    @SerializedName("completed")
    private boolean completed;

    @SerializedName("high_priority")
    private boolean highPriority;

    @SerializedName("categoryPk")
    private String categoryPk; // The backend sends just the PK of the category

    @SerializedName("characterPk")
    private String characterPk; // Added for Room filtering

    private long localCreatedOn;

    @Ignore
    private boolean justToggledPriority = false;

    @Ignore
    private boolean justToggledCompleted = false;

    public void setJustToggledPriority(boolean toggled) {
        this.justToggledPriority = toggled;
    }

    public void setJustToggledCompleted(boolean toggled) {
        this.justToggledCompleted = toggled;
    }

    public boolean isJustToggledPriority() {
        return justToggledPriority;
    }

    public boolean isJustToggledCompleted() {
        return justToggledCompleted;
    }


    // --- Constructors ---

    public Task() {
        this.localCreatedOn = System.currentTimeMillis();
    }


    public Task(String pk, String title, String description, String priority, String status) {
        this.pk = pk;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.localCreatedOn = System.currentTimeMillis();
    }

    // --- Getters and Setters ---

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public String getCategoryPk() {
        return categoryPk;
    }

    public void setCategoryPk(String categoryPk) {
        this.categoryPk = categoryPk;
    }

    public String getCharacterPk() {
        return characterPk;
    }

    public void setCharacterPk(String characterPk) {
        this.characterPk = characterPk;
    }

    public long getLocalCreatedOn() {
        return localCreatedOn;
    }

    public void setLocalCreatedOn(long localCreatedOn) {
        this.localCreatedOn = localCreatedOn;
    }
}
