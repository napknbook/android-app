package com.accelerate.napknbook.models;

public class UpdateTaskRequestBody {

    private String title;
    private String description;
    private String due_date;  // ISO 8601 string, e.g., "2025-04-04T10:00:00Z"
    private boolean completed;
    private String priority;  // "low", "medium", "high"
    private String status;    // "pending", "in-progress", "completed"
    private String categoryPk;

    public UpdateTaskRequestBody() {
    }

    public UpdateTaskRequestBody(String title, String description, String due_date, boolean completed, String priority, String status, String categoryPk) {
        this.title = title;
        this.description = description;
        this.due_date = due_date;
        this.completed = completed;
        this.priority = priority;
        this.status = status;
        this.categoryPk = categoryPk;
    }

    // --- Getters and Setters ---

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

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
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

    public String getCategoryPk() {
        return categoryPk;
    }

    public void setCategoryPk(String categoryPk) {
        this.categoryPk = categoryPk;
    }
}
