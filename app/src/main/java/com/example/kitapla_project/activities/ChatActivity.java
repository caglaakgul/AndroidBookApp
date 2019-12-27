package com.example.kitapla_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitapla_project.R;
import com.example.kitapla_project.adapters.AdapterChat;
import com.example.kitapla_project.models.Chat;
import com.example.kitapla_project.models.User;
import com.example.kitapla_project.notifications.APIService;
import com.example.kitapla_project.notifications.Client;
import com.example.kitapla_project.models.Data;
import com.example.kitapla_project.notifications.Response;
import com.example.kitapla_project.models.Sender;
import com.example.kitapla_project.models.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIV;
    TextView hisNameTV, userStatusTV;
    EditText msgET;
    ImageButton sendMsgIB;

    APIService apiService;
    boolean notify = false;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDbRef;

    //seen message or not
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<Chat> chatList;
    AdapterChat adapterChat;

    String hisUid, myUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        inits();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //create api service
        apiService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        Bundle bundle = getIntent().getExtras();
        String hisName = bundle.getString("nameSurname");
        hisNameTV.setText(hisName);

        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDbRef = firebaseDatabase.getReference("Users");

        Bundle bundle1 = getIntent().getExtras();
        String online_status = bundle.getString("onlineStatus");
        //userStatusTV.setText(online_status);
        if(online_status.equals("online"))
            userStatusTV.setText(online_status);
        else {
//convert time stamp to dd/mm/yy hh:mm
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(online_status));
            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm",cal).toString();
            userStatusTV.setText("Son görülme "+dateTime);
        }


        clicks();
        readMessages();
        seenMessage();

    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        HashMap<String , Object> hassSeenHashMap = new HashMap<>();
                        hassSeenHashMap.put("isSeen",true);
                        ds.getRef().updateChildren(hassSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    adapterChat = new AdapterChat(ChatActivity.this,chatList);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recyclerview
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void inits() {
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView = findViewById(R.id.chat_recyclerView);
        profileIV = findViewById(R.id.profileIV);
        hisNameTV = findViewById(R.id.userNameTV);
        userStatusTV = findViewById(R.id.userStatusTV);
        msgET = findViewById(R.id.msgET);
        sendMsgIB = findViewById(R.id.sendMsgIB);
    }

    public void clicks(){
        sendMsgIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                //get text from edit text
                String message = msgET.getText().toString().trim();

                //check if text is empty or not
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Lütfen ileti giriniz.", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }
                msgET.setText("");
            }
        });

        
    }

    private void sendMessage(String message) {

        String timeStamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("timeStamp",timeStamp);
        hashMap.put("isSeen",false);

        databaseReference.child("Chats").push().setValue(hashMap);


        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(notify){
                        sendNotification(hisUid,user.getNameSurname(),message);
                    }
                    notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String hisUid, String nameSurname, String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference( "Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUid,nameSurname+":"+message, "Yeni Mesaj", hisUid);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(ChatActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            myUid = firebaseUser.getUid();

        } else { //user not signed in go SignupPage
            startActivity(new Intent(this, LoginPage.class));
            finish();
        }
    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        //update value
        dbRef.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        //set value
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //get timeStamp
        String timestamp = String.valueOf(System.currentTimeMillis());
        //set offline
        checkOnlineStatus(timestamp);
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        //set value
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                //Intent from fragment to Activity
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginPage.class));
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}

