package com.accelerate.napknbook.models;

public class CreateTaskRequestBody {

    private String title;
    private String description;
    private String due_date;        // ISO 8601 format, e.g., "2025-04-02T15:30:00Z"
    private boolean high_priority;

    private String priority = "medium";
    private String status = "pending";

    private String categoryPk;
    private String characterPk;

    public CreateTaskRequestBody() {}

    public CreateTaskRequestBody(String title, String description, String due_date, boolean high_priority,
                                 String priority, String status, String categoryPk, String characterPk) {
        this.title = title;
        this.description = description;
        this.due_date = due_date;
        this.high_priority = high_priority;
        this.priority = priority;
        this.status = status;
        this.categoryPk = categoryPk;
        this.characterPk = characterPk;
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

    public boolean isHigh_priority() {
        return high_priority;
    }

    public void setHigh_priority(boolean high_priority) {
        this.high_priority = high_priority;
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

    public String getCharacterPk() {
        return characterPk;
    }

    public void setCharacterPk(String characterPk) {
        this.characterPk = characterPk;
    }
}
