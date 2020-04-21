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
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private CircleImageView profileImage;
    private EditText profileNick;
    private ActionBar actionBar;
    private ImageView Profile_Indicator;
    private ConstraintLayout Button_AddNewWrok;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

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
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        profileImage = findViewById(R.id.ImageView_Profile);
        profileNick = findViewById(R.id.EditText_ShowNick);
        Button_AddNewWrok = findViewById(R.id.Button_AddNewWrok);
        Button_AddNewWrok.setClickable(true);
        profileNick.setFocusable(false);
        profileNick.setClickable(false);
        profileNick.setText(firebaseAuth.getCurrentUser().getDisplayName().toString());
        profileNick.setTextSize(35);
        Thread mThread= new Thread(){
            @Override
            public void run() {
                try{
                    //현재로그인한 사용자 정보를 통해 PhotoUrl 가져오기
                    URL url = new URL(user.getPhotoUrl().toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                } catch (MalformedURLException ee) {
                    ee.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try{
            mThread.join();
            profileImage.setImageBitmap(bitmap);
        }catch (InterruptedException e){
            e.printStackTrace();
        }


    }

    public void onclick(View view) {
        switch (view.getId()){
            case R.id.ImageView_Profile:
                break;
            case R.id.Button_AddNewWrok:
                Intent new_work = new Intent(ProfileActivity.this, AddnewpostActivity.class);
                startActivity(new_work);
                break;
            case R.id.Button_Main:
                Intent main = new Intent(ProfileActivity.this, MainActivity.class);
                main.putExtra("로그인로그", "profile");
                startActivity(main);
                overridePendingTransition(0, 0);
                finish();
                break;
        }
    }
}
