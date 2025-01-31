package com.example.kitapla_project.notifications;

import com.example.kitapla_project.models.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
        if(user != null){
            updateToken(tokenRefresh);
        }

    }

    private void updateToken(String tokenRefresh) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        ref.child(user.getUid()).setValue(token);
    }
}
