package com.example.audiomanage.models;

public class AudioModel {
    private String id;
    private String name;
    private String url;
    private String user_Id;
    private String status;

    public AudioModel() {
    }

    public AudioModel(String id, String name, String url, String user_Id, String status) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.user_Id = user_Id;
        this.status = status;
    }

    public String getUser_Id() {
        return user_Id;
    }

    public void setUser_Id(String user_Id) {
        this.user_Id = user_Id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
