package com.example.kitapla_project.models;

import java.io.Serializable;

public class User implements Serializable {
    String nameSurname;
    String province;
    String email;
    String password;
    String uid;
    String imgPath;
    String onlineStatus;

    public User() {
    }

    public User(String nameSurname, String province, String email, String password, String uid, String imgPath, String onlineStatus){
        this.nameSurname = nameSurname;
        this.province = province;
        this.email = email;
        this.password = password;
        this.uid = uid;
        this.imgPath = imgPath;
        this.onlineStatus = onlineStatus;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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
}
