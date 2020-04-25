package com.fbf.a2r;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private Bitmap bitmap;
    private SimpleDraweeView profileImage;
    private EditText profileNick;
    private ActionBar actionBar;
    private ImageView Profile_Indicator;
    private ConstraintLayout Button_AddNewWrok;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Button Button_MyPostNumber;
    private int click_count;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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
        Profile_Indicator = findViewById(R.id.ImageView_ProfileIndicator);
        Profile_Indicator.setBackgroundColor(Color.BLACK);
        firebaseAuth = firebaseAuth.getInstance();
        click_count = 0;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = firebaseDatabase.getReference().child("MyWorld").child("User").child(firebaseAuth.getCurrentUser().getUid());

        profileImage = findViewById(R.id.ImageView_Profile);
        profileNick = findViewById(R.id.EditText_ShowNick);
        Button_MyPostNumber = findViewById(R.id.Button_MyPostNumber);
        Button_AddNewWrok = findViewById(R.id.Button_AddNewWrok);
        Button_AddNewWrok.setClickable(true);
        profileNick.setFocusable(false);
        profileNick.setClickable(false);
        profileNick.setTextSize(35);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProfileDataSet profileDataSet = dataSnapshot.getValue(ProfileDataSet.class);
                setProfile(profileDataSet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void setProfile(ProfileDataSet profileDataSet){
        Button_MyPostNumber.setText("내 게시글 수\n" + profileDataSet.getMypostCount());
        profileImage.setImageURI(profileDataSet.getProfileImageUrl());
        profileNick.setText(profileDataSet.getProfileName());
    }
    public void onclick(View view) {
        switch (view.getId()){
            case R.id.Button_Profile_Setting:
                Toast.makeText(this, "프로필 설정", Toast.LENGTH_SHORT).show();
                Intent NewProfile = new Intent(getApplicationContext(), ProfileUpdateActivity.class);
                startActivity(NewProfile);
                break;
            case R.id.ImageView_Profile:
                break;
            case R.id.Button_AddNewWrok:
                Intent new_work = new Intent(ProfileActivity.this, AddnewpostActivity.class);
                startActivity(new_work);
                break;
            case R.id.Button_Main:
                if(click_count < 1) {
                    click_count += 1;
                    Intent main = new Intent(ProfileActivity.this, MainActivity.class);
                    main.putExtra("로그인로그", "GoogleLogin");
                    startActivity(main);
                    overridePendingTransition(0, 0);
                    finish();
                }
                break;
        }
    }
}
