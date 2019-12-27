package com.example.kitapla_project.models;

public class Token {
    /*FCM Token veya registrationToken
    ID, GCMconnection sunucuları tarafından
     */

    String token;

    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
