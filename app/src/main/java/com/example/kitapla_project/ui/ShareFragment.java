package com.example.kitapla_project.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TaskInfo;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.kitapla_project.R;
import com.example.kitapla_project.activities.BottomNavActivity;
import com.example.kitapla_project.activities.LoginPage;
import com.example.kitapla_project.activities.SignupPage;
import com.example.kitapla_project.models.Post;
import com.example.kitapla_project.models.User;
import com.example.kitapla_project.utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import gun0912.tedbottompicker.TedBottomPicker;

public class ShareFragment extends Fragment {
    private View v;
    private EditText edtPostText;
    private ImageView addIV;
    private Button btnShare;
    ProgressDialog pd;
    private String uid, email;

    private String mPost_key = null;

    private Uri imgUri;

    private DatabaseReference databasePosts;
    private DatabaseReference databaseUsers;
    private StorageReference mStorageReference;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_share, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        inits();

        databasePosts = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        pd = new ProgressDialog(getContext());

        checkUserStatus();

        clicks();
    }

    @Override
    public void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUserStatus();
    }

    private void clicks() {
        btnShare.setOnClickListener(v -> {
            pd.setMessage("Paylaşılıyor...");


            if (TextUtils.isEmpty(edtPostText.getText().toString().trim())) {
                Toast.makeText(getContext(), "Lütfen içerik giriniz.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!TextUtils.isEmpty(edtPostText.getText().toString().trim())) {
                pd.show();

                String photoName = UUID.randomUUID().toString();
                uploadImg(imgUri, photoName);


            }
        });

        //get image from camera/gallery on click
        addIV.setOnClickListener(v -> showImagePicker());


    }

    private void sendDataToDB(String imageName) {

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference reference2 = databasePosts.child(firebaseUser.getUid());
                DatabaseReference newPost = reference2.push();

                newPost.child("uid").setValue(firebaseUser.getUid());
                newPost.child("postText").setValue(edtPostText.getText().toString().trim());
                newPost.child("imagePath").setValue(imageName);
                newPost.child("username").setValue(Global.user.getNameSurname()).addOnCompleteListener(task -> {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Paylaşıldı", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigate(ShareFragmentDirections.actionNavigationShareToNavigationHome());
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void inits() {
        edtPostText = v.findViewById(R.id.edtPostText);
        addIV = v.findViewById(R.id.addIV);
        btnShare = v.findViewById(R.id.btnShare);
    }

    private void showImagePicker() {
        if (getContext() == null)
            return;

        TedPermission.with(getContext())
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        checkPhoto();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .check();
    }

    private void checkPhoto() {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(getContext())
                .setImageProvider((imageView, imageUri) -> {
                    Glide.with(getContext()).load(imageUri).into(imageView);
                })
                .setOnImageSelectedListener(uri -> {
                            imgUri = uri;
                            Glide.with(getContext()).load(uri).into(addIV);

                        }
                )
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .create();

        bottomSheetDialogFragment.show(getChildFragmentManager());
    }

    private void uploadImg(Uri uri, String imageName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://kitapla.appspot.com");
        StorageReference mountainsRef = storageRef.child(imageName);


        mStorageReference = FirebaseStorage.getInstance().getReference();
        mStorageReference.child("PostImage");
        mountainsRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        sendDataToDB(imageName);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("ShareFragmentTest", e.getMessage());
            }
        });
    }


    private void checkUserStatus() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            email = firebaseUser.getEmail();
            uid = firebaseUser.getUid();

        } else { //user not signed in go SignupPage
            startActivity(new Intent(getActivity(), LoginPage.class));
            getActivity().finish();
        }

    }
}