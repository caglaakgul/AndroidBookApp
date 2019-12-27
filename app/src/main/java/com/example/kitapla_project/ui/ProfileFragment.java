package com.example.kitapla_project.ui;


import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.kitapla_project.R;
import com.example.kitapla_project.activities.BottomNavActivity;
import com.example.kitapla_project.activities.LoginPage;
import com.example.kitapla_project.activities.UsersActivity;
import com.example.kitapla_project.models.GetPost;
import com.example.kitapla_project.models.User;
import com.example.kitapla_project.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

import gun0912.tedbottompicker.TedBottomPicker;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * A simple {@link Fragment} subclass.
 */

public class ProfileFragment extends Fragment {
    private ImageView userPic_iv;
    private TextView nameSurname_tv, email_tv, province_tv;
    private FloatingActionButton fab;

    private ProgressDialog progressDialog;

    private View v;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;
    private DatabaseReference databaseReference;

    //storage
    private StorageReference storageReference;


    //profil ya da arka planı/kapağı seçmek için
    String profilePhoto;

    public ProfileFragment() {
        // Required empty public constructor
    }

    Uri imgUri = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference(); //firebase storage reference

        init();

        progressDialog = new ProgressDialog(getActivity());

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String nameSurname = "" + ds.child("nameSurname").getValue();
                    String email = "" + ds.child("email").getValue();
                    String province = "" + ds.child("province").getValue();
                    String image = "" + ds.child("imgPath").getValue();

                    nameSurname_tv.setText(nameSurname);
                    email_tv.setText(email);
                    province_tv.setText(province);

                   /* StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://kitapla.appspot.com/"+image);
                    imgRef.getDownloadUrl().addOnSuccessListener
                            (uri -> Glide.with(getContext()).load(uri).placeholder(R.drawable.ic_default_img_white).into(userPic_iv));*/



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        clicks();
    }

    private void init() {
        email_tv = v.findViewById(R.id.email_tv);
        province_tv = v.findViewById(R.id.province_tv);
        nameSurname_tv = v.findViewById(R.id.nameSurname_tv);
        userPic_iv = v.findViewById(R.id.userPic_ImgView);
        fab = v.findViewById(R.id.fab);
    }

    private void clicks() {


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
    }


    public void showEditProfileDialog() {
        /**
         * edit profile picture
         * edit cover photo
         * edit name surname
         * edit province
         */
        String options[] = {"Profil Resmi", "Ad Soyad", "Şehir"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Düzenle");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //profil resmini düzenle
                    progressDialog.setMessage("Profiliniz güncelleniyor.");
                    profilePhoto = "image";
                    showImagePicker();
                } else if (which == 1) {
                    progressDialog.setMessage("Profiliniz güncelleniyor.");
                    showNameProvinceUpdateDialog("nameSurname");
                } else if (which == 2) {
                    progressDialog.setMessage("Profiliniz güncelleniyor.");
                    showNameProvinceUpdateDialog("province");
                }
            }
        });
        //create ve dialoğu göster:
        builder.create().show();
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
                    if(user.getUid().equals(user.getUid())){
                        /*StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://kitapla.appspot.com/");
                        imgRef.getDownloadUrl().addOnSuccessListener
                                (uri -> Glide.with(getContext()).load(uri).placeholder(R.drawable.ic_default_img_white).into(userPic_iv));*/
                    }


                    Glide.with(getContext()).load(imageUri).into(imageView);
                })
                .setOnImageSelectedListener(uri -> {
                            this.imgUri = uri;
                            Glide.with(getContext()).load(uri).into(userPic_iv);

                        }
                )
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .create();

        bottomSheetDialogFragment.show(getChildFragmentManager());
    }

    private void showNameProvinceUpdateDialog(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getKeyDisplayName(key));
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(Constants.dpToPx(getContext(), 20), 16, Constants.dpToPx(getContext(), 20), 16);

        final EditText editText = new EditText(getActivity());
        editText.setLayoutParams(lp);
        editText.setHint(nameSurname_tv.getText());
        linearLayout.addView(editText);

        builder.setView(linearLayout);
        //güncelleme butonu
        builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    progressDialog.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Güncellendi", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                    Toast.makeText(getActivity(), key + " giriniz", Toast.LENGTH_SHORT).show();
            }
        });
        //iptal butonu
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //dialogu oluştur ve göster
        builder.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); //fragment içinde menüyü göstermek için
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);

        MenuItem item = menu.findItem(R.id.list_users);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                //Intent from fragment to Activity
                firebaseAuth.signOut();
                startActivity(new Intent(getActivity(), LoginPage.class));
                return true;
            case R.id.list_users:
                Intent intent = new Intent(getActivity(), UsersActivity.class);
                startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }


    private String getKeyDisplayName(String key) {
        if (key.equals("nameSurname")) {
            return "Ad Soyad";
        } else if (key.equals("province")) {
            return "Şehir";
        } else {
            return "Güncelle";
        }
    }


}
