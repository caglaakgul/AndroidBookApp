package com.example.kitapla_project.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kitapla_project.R;
import com.example.kitapla_project.models.GetPost;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    private Context context;
    private List<GetPost> postList;

    public AdapterPosts(Context context, List<GetPost> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_posts.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int position) {
        String nameSurname = postList.get(position).getUsername();
        String uid = postList.get(position).getUid();
        String postText = postList.get(position).getPostText();
        String postTime = postList.get(position).getTime();
        String postImg = postList.get(position).getImagePath();



        //set data
        myHolder.nameSurnameTV.setText(nameSurname);
        myHolder.postTimeTV.setText(postTime);
        myHolder.postTV.setText(postText);


        //set post img
        StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://kitapla.appspot.com/"+postImg);
        imgRef.getDownloadUrl().addOnSuccessListener
                (uri -> Glide.with(context).load(uri).placeholder(R.drawable.ic_default_img_white).into(myHolder.postImgIV));


        myHolder.moreImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Daha fazlası", Toast.LENGTH_SHORT).show();
            }
        });
        myHolder.sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileIV, postImgIV;
        TextView nameSurnameTV, postTimeTV, postTV;
        ImageButton moreImgBtn;
        Button sendMessageBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileIV = itemView.findViewById(R.id.profileIV);
            postImgIV = (ImageView) itemView.findViewById(R.id.postImgIV);
            nameSurnameTV = itemView.findViewById(R.id.nameSurnameTV);
            postTimeTV = itemView.findViewById(R.id.postTimeTV);
            postTV = itemView.findViewById(R.id.postTV);
            moreImgBtn = itemView.findViewById(R.id.moreImgBtn);
            sendMessageBtn = itemView.findViewById(R.id.sendMessageBtn);

        }
    }
}