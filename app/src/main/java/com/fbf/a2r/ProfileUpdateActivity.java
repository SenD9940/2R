package com.fbf.a2r;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUpdateActivity extends AppCompatActivity {
    private EditText EditText_NewNick;
    private TextView TextView_NewNick, TextView_Update_Profile_Image;
    private CircleImageView ImageView_Update_Profile_Image;
    private static final int PICK_FROM_ALBUM = 0000;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private Uri PhotoUri;
    private String OldImageName;
    private String NewImageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        }
        view.getSystemUiVisibility();
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        firebaseAuth = FirebaseAuth.getInstance();
        getProfile();
        EditText_NewNick = findViewById(R.id.EditText_Update_Profile_NewNick);
        TextView_NewNick = findViewById(R.id.TextView_Update_Profile_Nick);
        PhotoUri = null;
        ImageView_Update_Profile_Image = findViewById(R.id.ImageView_Update_Profile_Image);
        TextView_Update_Profile_Image = findViewById(R.id.TextView_Update_Profile_Image);

    }

    private void getProfile(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.child("MyWorld").child("User").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                OldImageName = dataSnapshot.getValue(ProfileDataSet.class).getProfileImageName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteImage(String OldImageName){
        if(OldImageName != null){
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference().child("ProfileImage").child(OldImageName);
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }



    private void goToAlbum(){
        Intent Album = new Intent(Intent.ACTION_PICK);
        Album.setType(MediaStore.Images.Media.CONTENT_TYPE);
        Album.setType("image/*");
        startActivityForResult(Album, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FROM_ALBUM) {
            if(data != null){
                PhotoUri = data.getData();
                setImage();
            }
        }
    }

    private void setImage(){
        TextView_Update_Profile_Image.setVisibility(View.GONE);
        ImageView_Update_Profile_Image.setImageURI(PhotoUri);
    }

    private void FirebaseStorageUpload(boolean newImage){
        if(newImage == true){
            deleteImage(OldImageName);
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final String now = simpleDateFormat.format(date);
            String newName = EditText_NewNick.getText().toString();
            NewImageName = newName + now;
            storageReference = storageReference.child("ProfileImage/" + NewImageName);
            final UploadTask uploadTask = storageReference.putFile(PhotoUri);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("프로필 업데이트중 " + (int)progress + "%");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            updateProfile(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                }
            });
        }
        else{
            updateProfile(null);
        }

    }
    private void updateProfile(String Uri){
        String newName = EditText_NewNick.getText().toString();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference = databaseReference.child("MyWorld").child("User").child(firebaseAuth.getCurrentUser().getUid());
        if(Uri != null){
            Map<String, Object> update = new HashMap<>();
            if(newName != null){
                update.put("profileName", newName);
            }
            update.put("profileImageUrl", Uri);
            update.put("profileImageName",NewImageName);
            databaseReference.updateChildren(update);
        }
        else{
            Map<String, Object> update = new HashMap<>();
            update.put("profileName", newName);
            databaseReference.updateChildren(update);
        }
        finish();

    }

    public boolean isChanged(){
        if(EditText_NewNick.getText().toString().length() != 0 || PhotoUri != null){
            return true;
        }else{
            Toast.makeText(this, "변경할것이 없습니다", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void onclick(View view) {
        switch (view.getId()){
            case R.id.ImageView_Update_Profile_Image:
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.imageview_preview_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.Delete_Image:
                                TextView_Update_Profile_Image.setVisibility(View.VISIBLE);
                                ImageView_Update_Profile_Image.setImageURI(null);
                                PhotoUri = null;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                break;
            case R.id.TextView_Update_Profile_Image:
                goToAlbum();
                break;
            case R.id.Button_Update_Profile:
                if(isChanged()) {
                    if (PhotoUri != null) {
                        FirebaseStorageUpload(true);
                    } else {
                        FirebaseStorageUpload(false);
                    }
                }

                break;
            case R.id.TextView_Update_Profile_Nick:
                TextView_NewNick.setVisibility(View.GONE);
                EditText_NewNick.setVisibility(View.VISIBLE);
                break;
        }
    }
}
