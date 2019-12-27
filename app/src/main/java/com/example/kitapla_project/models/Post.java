package com.example.kitapla_project.models;

public class Post {
    String nameSurname;
    String uid;
    //String email;
    String postText;
    //String postImg;

    public Post(){

    }

    public Post(String nameSurname, String uid, String postText) {
        this.nameSurname = nameSurname;
        this.uid = uid;
        this.postText = postText;
        //this.postImg = postImg;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
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

   /* public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }*/
}
