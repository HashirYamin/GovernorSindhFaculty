package com.example.governorsindhfaculty.model;

public class LinkModel {
    private String linkDescription;
    private String linkUrl;
    public LinkModel() {
        // Default constructor required for calls to DataSnapshot.getValue(LinkModel.class)
    }

    public LinkModel(String linkDescription, String linkUrl) {
        this.linkDescription = linkDescription;
        this.linkUrl = linkUrl;
    }

    public String getLinkDescription() {
        return linkDescription;
    }

    public void setLinkDescription(String linkDescription) {
        this.linkDescription = linkDescription;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
