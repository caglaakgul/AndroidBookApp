package com.example.kitapla_project.models;

public class GetPost {
    String username, uid, postText, time, imagePath;

    public GetPost(){

    }

    public GetPost(String username, String uid, String postText, String time, String imagePath) {
        this.username = username;
        this.uid = uid;
        this.postText = postText;
        this.time = time;
        this.imagePath = imagePath;
    }

    public String getTime() {
        return time;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String img) {
        this.imagePath = img;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }
}