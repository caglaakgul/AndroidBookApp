package com.example.kitapla_project.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kitapla_project.R;
import com.example.kitapla_project.UserManager;
import com.example.kitapla_project.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupPage extends AppCompatActivity {
    public TextView nameSurname_tv, email_tv, password_tv, go_login;
    public Button signup_btn;
    public Spinner provincesSpinner;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    FirebaseDatabase db;

    public String[] provinces = {"Bilecik", "İstanbul", "İzmir"};

    public ArrayAdapter<String> provinceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        db = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        init();

        progressBar = new ProgressBar(this);


        spinnerAdapter();
        btn_clicked();


    }

    protected void init() {
        nameSurname_tv = (TextView) findViewById(R.id.nameSurname_tv);
        email_tv = (TextView) findViewById(R.id.signup_email_tv);
        password_tv = (TextView) findViewById(R.id.signup_password_tv);
        signup_btn = (Button) findViewById(R.id.signup_button);
        go_login = (TextView) findViewById(R.id.go_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        provincesSpinner = (Spinner) findViewById(R.id.spinner_province);
    }


    private void spinnerAdapter() {
        provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinces);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provincesSpinner.setAdapter(provinceAdapter);
    }

    public void btn_clicked() {
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameSurname = nameSurname_tv.getText().toString().trim();
                String province = provincesSpinner.getSelectedItem().toString().trim();
                String email = email_tv.getText().toString().trim();
                String password = password_tv.getText().toString().trim();
                String onlineStatus = "online";

                if (TextUtils.isEmpty(nameSurname)) {
                    Toast.makeText(getApplicationContext(), "Lütfen ad soyad bilgilerinizi girin.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(province)) {
                    Toast.makeText(getApplicationContext(), "Lütfen il bilginizi seçin.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Lütfen e-mail adresinizi girin.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Lütfen bir parola girin.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Parola 6 karakterden az olamaz.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignupPage.this, "Kayıt başarısız oldu." + task.getException(), Toast.LENGTH_SHORT).show();
                        } else {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                           /* FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Users");*/

                            String email = user.getEmail();
                            String uid = user.getUid();

                            String image = "";

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                            reference.child(user.getUid()).setValue(new User(
                                    nameSurname, province, email, password, uid, image, onlineStatus));


                            new UserManager().setUser();
                            startActivity(new Intent(SignupPage.this, BottomNavActivity.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_loginPage = new Intent(SignupPage.this, LoginPage.class);
                startActivity(go_loginPage);
                finish();
            }
        });
    }

}