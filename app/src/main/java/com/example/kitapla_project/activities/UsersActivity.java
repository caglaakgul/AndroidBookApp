package com.example.kitapla_project.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitapla_project.R;
import com.example.kitapla_project.adapters.AdapterAllUsers;
import com.example.kitapla_project.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    AdapterAllUsers adapterAllUsers;
    List<User> usersList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_users);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tüm Kullanıcılar");

        inits();
        usersList = new ArrayList<>();
        getAllUsers();
    }

    public void inits(){
        recyclerView = findViewById(R.id.usersRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));

    }

    private void getAllUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    if(!user.getUid().equals(firebaseUser.getUid()))
                        usersList.add(user);
                    adapterAllUsers = new AdapterAllUsers(getApplication(), usersList);
                    recyclerView.setAdapter(adapterAllUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
