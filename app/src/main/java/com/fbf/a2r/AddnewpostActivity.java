package com.fbf.a2r;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
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

import org.w3c.dom.Comment;

import java.io.File;
import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddnewpostActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText EditText_PostTitle, EditText_PostContents;
    private ActionBar actionBar;
    private final int PERMISSIONS_REQUEST_RESULT = 1;
    private static final int PICK_FROM_ALBUM = 0000;
    private String[] permission_list;
    private Uri PhotoUri = null;
    private String UserID, Artist, CommentOption, Category;
    private ImageView imageView_Preview;
    private Button Button_allowComment, Button_NotallowComment;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private UploadTask uploadTask;
    private String DownloadURL;
    private String ViewCount = "0";
    private String AdViewCount = "0";
    private ArrayList<ProfileDataSet> profileDataSet;
    private Spinner Spinner_Category;
    private String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnewpost);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        PhotoUri = null;
        getProfile();

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
        CommentOption = "1";
        EditText_PostTitle = findViewById(R.id.EditText_AddPostTitle);
        EditText_PostContents = findViewById(R.id.EditText_AddPostContents);

        firebaseAuth = FirebaseAuth.getInstance();
        Artist = firebaseAuth.getCurrentUser().getDisplayName();
        UserID = firebaseAuth.getCurrentUser().getUid();
        downloadProfileImage();
        permission_list = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        checkPermissions();
        Button_allowComment = findViewById(R.id.Button_AllowComment);
        Button_NotallowComment = findViewById(R.id.Button_NotAllowComment);
        Spinner_Category = findViewById(R.id.Spinner_Category);
        imageView_Preview = findViewById(R.id.ImageView_File);
        imageView_Preview.setClickable(true);
        imageView_Preview.setEnabled(true);
        imageView_Preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.imageview_preview_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.Delete_Image:
                                imageView_Preview.setImageURI(null);
                                PhotoUri = null;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        Button_allowComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Button_allowComment.setBackgroundColor(Color.LTGRAY);
                    Button_NotallowComment.setBackgroundResource(R.drawable.button_allowcomment);
                    CommentOption = "0";
                }
                return false;
            }
        });

        Button_NotallowComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Button_NotallowComment.setBackgroundColor(Color.LTGRAY);
                    Button_allowComment.setBackgroundResource(R.drawable.button_allowcomment);
                    CommentOption = "1";
                }
                return false;
            }
        });

        Spinner_Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category = (String) parent.getItemAtPosition(position);
                Log.d("category", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Spinner_Category.setSelection(0);
                Category = (String) parent.getItemAtPosition(0);
            }
        });

    }

    public void checkPermissions(){
        for(String permission : permission_list){
            int chk = checkCallingOrSelfPermission(permission);
            if(chk == PackageManager.PERMISSION_DENIED){
                requestPermissions(permission_list, PERMISSIONS_REQUEST_RESULT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(PERMISSIONS_REQUEST_RESULT == requestCode){
            for(int i = 0; i < grantResults.length; i++){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "권한 요청이 되었습니다", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(this, "권한 요청을 해주세요", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
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
        imageView_Preview.setImageURI(PhotoUri);
    }
    private void getProfile(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        databaseReference.child("MyWorld").child("User").child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProfileDataSet profileDataSet = dataSnapshot.getValue(ProfileDataSet.class);
                UserName = profileDataSet.getProfileName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addPost(String Title, String Contents, String Artist, String UserID, String CommentOption, String ViewCount, String AdViewCount, String Category){
        String ProfileImage = this.profileDataSet.get(0).getProfileImageUrl();
        if(Title.length() == 0){
            Toast.makeText(this, "제목을 입력하세요", Toast.LENGTH_LONG).show();
        }else {
            if (PhotoUri != null) {
                UploadImage();
            } else {
                PostDataSet postDataSet = new PostDataSet(Title, Contents, Artist, CommentOption, ViewCount, AdViewCount, UserID, ProfileImage, Category);
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                databaseReference = databaseReference.child("MyWorld").child("NewPost");
                databaseReference.push().setValue(postDataSet).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddnewpostActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                        getProfile("add");
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddnewpostActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    public void UploadImage(){
        final String ProfileImage = this.profileDataSet.get(0).getProfileImageUrl();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String now = simpleDateFormat.format(date);
        if(PhotoUri != null){
            storageReference = storageReference.child("PostImage/" + UserName + now);
            uploadTask = storageReference.putFile(PhotoUri);

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("이미지 업로드 중");
            progressDialog.show();

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("이미지 업로드중 " + (int)progress + "%");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DownloadURL = uri.toString();
                            Log.d("다운로드", DownloadURL);
                            progressDialog.dismiss();
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase.getReference();
                            databaseReference = databaseReference.child("MyWorld").child("NewPost");
                            PostDataSet postDataSet = new PostDataSet(EditText_PostTitle.getText().toString(), EditText_PostContents.getText().toString(), Artist, CommentOption, DownloadURL, ViewCount, AdViewCount, UserID, ProfileImage, Category, Artist + now);
                            databaseReference.push().setValue(postDataSet).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddnewpostActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                                    PhotoUri = null;
                                    getProfile("add");
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddnewpostActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AddnewpostActivity.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.ImageView_Back:
                finish();
                break;
            case R.id.Button_Upload:
                addPost(EditText_PostTitle.getText().toString(), EditText_PostContents.getText().toString(), UserName, UserID, CommentOption, ViewCount, AdViewCount, Category);
                break;
            case R.id.ImageButton_AddPicutre:
                goToAlbum();
                break;
        }
    }
    public void addProfile(ProfileDataSet profileDataSet){
        this.profileDataSet.add(profileDataSet);
    }

    public void downloadProfileImage(){
        this.profileDataSet = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference("MyWorld").child("User").child(UserID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addProfile(dataSnapshot.getValue(ProfileDataSet.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void getProfile(final String Option){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("MyWorld").child("User").child(firebaseAuth.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProfileDataSet profileDataSet = dataSnapshot.getValue(ProfileDataSet.class);
                UpdateProfile(profileDataSet, Option);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } public void UpdateProfile(ProfileDataSet profileDataSet, String Option){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("MyWorld").child("User").child(UserID);
        Map<String, Object> mypostcount = new HashMap<>();
        if(Option.equals("add")){
            mypostcount.put("mypostCount", String.valueOf(Integer.parseInt(profileDataSet.getMypostCount()) + 1));
        }
        else if(Option.equals("delete")){
            mypostcount.put("mypostCount", String.valueOf(Integer.parseInt(profileDataSet.getMypostCount()) - 1));
        }
        databaseReference.updateChildren(mypostcount);
    }
}
