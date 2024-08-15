package com.example.governorsindhfaculty.model;

public class AssignmentModel {
    private String title;
    private String description;
    private String type; // "link", "pdf", or "word"
    private String url;

    // Default constructor required for calls to DataSnapshot.getValue(AssignmentModel.class)
    public AssignmentModel() {}
    public AssignmentModel(String title, String description, String type, String url) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.url = url;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
