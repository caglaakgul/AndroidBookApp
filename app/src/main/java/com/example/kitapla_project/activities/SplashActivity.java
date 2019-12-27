package com.example.kitapla_project.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kitapla_project.R;
import com.example.kitapla_project.UserManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    UserManager userManager;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userManager = new UserManager();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
               loginControl();
            }
        }, 2000);
    }

    private void loginControl(){
        if (firebaseUser != null) {
            Intent i = new Intent(this, BottomNavActivity.class);
            startActivity(i);
            userManager.setUser();
            finish();
        } else {
            Intent i = new Intent(this, LoginPage.class);
            startActivity(i);
            finish();
        }
    }
}
