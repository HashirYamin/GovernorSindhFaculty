package com.example.governorsindhfaculty.model;

public class YouTubeVideoModel {
    private String videoDescription;
    private String videoUrl;

    public YouTubeVideoModel() {
        // Default constructor required for calls to DataSnapshot.getValue(YouTubeVideoModel.class)
    }

    public YouTubeVideoModel(String videoDescription, String videoUrl) {
        this.videoDescription = videoDescription;
        this.videoUrl = videoUrl;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
