package com.example.audiomanage.models;

public class UserModel {
    private String id;
    private String name;
    private String email;
    private String password;
    private String select_Audio;

    public UserModel() {
    }

    public UserModel(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
    public UserModel(String id, String name, String email, String password, String select_Audio) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.select_Audio = select_Audio;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getSelect_Audio() {
        return select_Audio;
    }

    public void setSelect_Audio(String select_Audio) {
        this.select_Audio = select_Audio;
    }
}
