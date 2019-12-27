package com.example.kitapla_project.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitapla_project.R;
import com.example.kitapla_project.adapters.AdapterPosts;
import com.example.kitapla_project.models.GetPost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private View v;

    RecyclerView recyclerView;
    List<GetPost> postList;
    AdapterPosts adapterPosts;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.fragment_home, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        inits();
        loadPosts();
    }

    private void loadPosts() {
        //path of all posts
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    for (DataSnapshot children : ds.getChildren()){
                        GetPost modelGetPost = children.getValue(GetPost.class);
                        postList.add(modelGetPost);
                    }

                }
                //adapter
                adapterPosts = new AdapterPosts(getActivity(),postList);
                //set adapter
                recyclerView.setAdapter(adapterPosts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }

    public void inits(){
        recyclerView = v.findViewById(R.id.postsRecyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);
        postList = new ArrayList<>();

    }

}