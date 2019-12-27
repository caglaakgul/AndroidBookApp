package com.example.kitapla_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kitapla_project.R;
import com.example.kitapla_project.UserManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity {

    private TextView login_email, login_password;
    private Button login_button;
    private TextView go_signup;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        firebaseAuth = FirebaseAuth.getInstance();
        init();
        goSignupClicked();
        login_btn_clicked();

    }

    protected void init() {
        login_email = (TextView) findViewById(R.id.login_email_tv);
        login_password = (TextView) findViewById(R.id.login_password_tv);
        login_button = (Button) findViewById(R.id.login_button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        go_signup = (TextView) findViewById(R.id.go_signup);
    }

    public void login_btn_clicked() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_email.getText().toString();
                final String password = login_password.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    login_email.setError("Invalid email");
                    login_email.setFocusable(true);
                    progressBar.setVisibility(View.GONE);
                } else {
                    loginUser(email, password);
                }


            }
        });
    }

    private void loginUser(String email, String password) {
        String onlineStatus = "online";
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginPage.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginPage.this, "Giriş başarılı!", Toast.LENGTH_LONG).show();
                            if (firebaseAuth.getCurrentUser() != null) {
                                startActivity(new Intent(LoginPage.this, BottomNavActivity.class));
                                new UserManager().setUser();
                                finish();
                            }
                        } else
                            Toast.makeText(LoginPage.this, "E-mail veya parola yanlış!", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginPage.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void goSignupClicked() {
        go_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_signupPage = new Intent(LoginPage.this, SignupPage.class);
                startActivity(go_signupPage);
            }
        });
    }

}
