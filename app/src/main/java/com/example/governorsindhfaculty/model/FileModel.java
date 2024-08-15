package com.example.governorsindhfaculty.model;

public class FileModel {
    String filename, fileUrl;

    public FileModel(){

    }

    public FileModel(String filename, String fileUrl) {
        this.filename = filename;
        this.fileUrl = fileUrl;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
