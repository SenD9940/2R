package com.fbf.a2r;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static android.R.*;
import static android.R.color.black;
import static android.R.color.darker_gray;

public class MainActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private String myName, Loginlog = "";
    private RecyclerView RecyclerView_CardView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String Position;
    private ConstraintLayout Button_ShowProfile;
    private PostDataSet postDataSet;
    private ImageView Profile_Indicator, Main_Indicator, ImageView_Category;
    private ArrayList<PostDataSet> postDataSets;
    private ArrayList<String> postKeys;
    public static Activity _MainActivity;
    private SimpleDraweeView SimpleDraweeViewProfileImage;
    private String getCategory;
    private Toolbar toolbar;
    private DrawerLayout mdrawerLayout;
    private TextView TextView_Drawer_ProfileName;
    private int click_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        Intent getLoginlog = getIntent();
        actionBar = getSupportActionBar();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        Button_ShowProfile = findViewById(R.id.Button_Profile);
        Profile_Indicator = findViewById(R.id.ImageView_ProfileIndicator);
        TextView_Drawer_ProfileName = findViewById(R.id.TextView_Drawer_ProfileName);
        Main_Indicator = findViewById(R.id.ImageView_MainIndicator);
        Main_Indicator.setBackgroundColor(Color.BLACK);
        SimpleDraweeViewProfileImage = findViewById(R.id.SimpleDraweeView_Drawer_ProfileImage);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        click_count = 0;
        mdrawerLayout = findViewById(R.id.drawer_layout);
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
        Loginlog = getLoginlog.getStringExtra("로그인로그");
        // specify an adapter (see also next example)
        if (!Loginlog.equals("guestlogin")) {
            firebaseAuth = firebaseAuth.getInstance();
            myName = firebaseAuth.getCurrentUser().getDisplayName();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            SimpleDraweeViewProfileImage.setImageURI(firebaseUser.getPhotoUrl());
            TextView_Drawer_ProfileName.setText(firebaseUser.getDisplayName());

        }
        post(null);
        Button_ShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(click_count < 1){
                    click_count += 1;
                    Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(profile);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        });
        setCategory(getCategory);
    }

    public void setCategory(String getCategory){
        if(getCategory != null){
            post(getCategory);
        }
    }

    public void post(final String category){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("로딩중...");
        progressDialog.show();
        Intent intent = getIntent();
        Position = intent.getStringExtra("position");
        RecyclerView_CardView = findViewById(R.id.RecyclerView_CardView);
        layoutManager = new LinearLayoutManager(this);
        RecyclerView_CardView.setHasFixedSize(true);
        RecyclerView_CardView.setLayoutManager(layoutManager);
        postKeys = new ArrayList<>();
        postDataSets = new ArrayList<>();
        databaseReference.child("MyWorld").child("NewPost").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postDataSets.clear();
                for(DataSnapshot Snapshot : dataSnapshot.getChildren()) {
                    postDataSet = Snapshot.getValue(PostDataSet.class);
                    if(category != null){
                        if(category.equals(postDataSet.getCategory())){
                            Log.d("post", String.valueOf(postDataSet));
                            postDataSets.add(0, postDataSet);
                            postKeys.add(0, Snapshot.getKey());
                            mAdapter.notifyDataSetChanged();
                        }
                    }else{
                        Log.d("post", String.valueOf(postDataSet));
                        postDataSets.add(0, postDataSet);
                        postKeys.add(0, Snapshot.getKey());
                        mAdapter.notifyDataSetChanged();
                    }

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mAdapter = new CardViewAdapter(postDataSets, postKeys, MainActivity.this);
        RecyclerView_CardView.setAdapter(mAdapter);
    }


    @Override
    public void onBackPressed() {
        if(mdrawerLayout.isDrawerOpen(GravityCompat.START)){
            mdrawerLayout.closeDrawers();
        }
        else{
            super.onBackPressed();
        }
    }

    public void onclick(View view) {
        switch (view.getId()){
            case R.id.Button_Honor:
                Intent Honor = new Intent(this, ShowHonorListActivity.class);
                startActivity(Honor);
                overridePendingTransition(0, 0);
                finish();

                break;
            case R.id.Button_Main:
                getCategory = null;
                post(getCategory);
                break;
            case R.id.Button_Category_Illu:
                getCategory = "일러스트";
                post(getCategory);
                onBackPressed();
                break;
            case R.id.Button_Category:
                mdrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.Button_Refresh:
                post(getCategory);
                Toast.makeText(this, "새로고침 되었습니다", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Button_Main_Menu:
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.actionbar_actions, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_logout:
                                firebaseAuth.signOut();
                                Toast.makeText(MainActivity.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                                Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(logout);
                                finish();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
                break;
            case R.id.Button_AddNewWrok:
                Intent addWork = new Intent(this, AddnewpostActivity.class);
                startActivity(addWork);
                break;
        }
    }
}
