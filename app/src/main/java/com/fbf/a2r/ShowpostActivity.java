package com.fbf.a2r;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ShowpostActivity extends AppCompatActivity {

    private TextView TextView_ShowPost_Title, TextView_ShowPost_Contents, TextView_ShowPost_Author, TextView_ShowPost_ViewCount;
    private SimpleDraweeView SimpleDraweeView_ShowPost_PostImage;
    private ImageButton ImageButton_ShowPost_Menu;
    private String Title, Contents, Author, GetDownloadUrl, ViewCount, Key, CommentOption, UserID, Context, Position, ImageName;
    private ActionBar actionBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private PostDataSet postDataSet;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showpost);
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
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("MyWorld").child("NewPost");
        TextView_ShowPost_Title = findViewById(R.id.TextView_ShowPost_Title);
        TextView_ShowPost_Contents = findViewById(R.id.TextView_ShowPost_Contents);
        TextView_ShowPost_Author = findViewById(R.id.TextView_ShowPost_Author);
        TextView_ShowPost_ViewCount = findViewById(R.id.TextView_ShowPost_ViewCount);
        SimpleDraweeView_ShowPost_PostImage = findViewById(R.id.SimpleDraweeView_ShowPost_PostImage);
        ImageButton_ShowPost_Menu = findViewById(R.id.ImageButton_ShowPost_Menu);
        final Intent post = getIntent();
        Title = post.getStringExtra("Title");
        Contents = post.getStringExtra("Contents");
        Author = post.getStringExtra("Author");
        GetDownloadUrl = post.getStringExtra("Image");
        ViewCount = post.getStringExtra("ViewCount");
        Key = post.getStringExtra("Key");
        CommentOption = post.getStringExtra("CommentOption");
        UserID = post.getStringExtra("Uid");
        Position = post.getStringExtra("Position");
        Context = post.getStringExtra("Context");
        ImageName = post.getStringExtra("ImageName");
        postDataSet = new PostDataSet();
        databaseReference.child(Key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Update(dataSnapshot.getValue(PostDataSet.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TextView_ShowPost_Title.setText(Title);
        TextView_ShowPost_Contents.setText(Contents);
        TextView_ShowPost_Author.setText(Author);
        TextView_ShowPost_ViewCount.setText(ViewCount);
        getProfile("UpdatePostViewCount");

        if(GetDownloadUrl != null){
            SimpleDraweeView_ShowPost_PostImage.setVisibility(View.VISIBLE);
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(GetDownloadUrl))
                    .build();
            SimpleDraweeView_ShowPost_PostImage.setController(Fresco.newDraweeControllerBuilder()
                    .setOldController(SimpleDraweeView_ShowPost_PostImage.getController())
                    .setLowResImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .setControllerListener(new BaseControllerListener() {
                        @Override
                        public void onIntermediateImageSet(String id, @Nullable Object imageInfo) {
                            super.onIntermediateImageSet(id, imageInfo);
                            updateViewSize(SimpleDraweeView_ShowPost_PostImage, (ImageInfo) imageInfo);
                        }

                        @Override
                        public void onFinalImageSet(String id, @Nullable Object imageInfo, @Nullable Animatable animatable) {
                            super.onFinalImageSet(id, imageInfo, animatable);
                            updateViewSize(SimpleDraweeView_ShowPost_PostImage, (ImageInfo) imageInfo);
                        }
                    })
                    .build());
        }else{
            SimpleDraweeView_ShowPost_PostImage.setVisibility(View.GONE);
        }

        ImageButton_ShowPost_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.showpost_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.Delete_Post:
                                if(GetDownloadUrl == null){
                                    DeletePost();
                                }else{
                                    DeleteImage();
                                }
                                break;
                            case R.id.Update_Post:
                                Toast.makeText(ShowpostActivity.this, "아직 개발중 입니다", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }
    void updateViewSize(SimpleDraweeView view, @Nullable ImageInfo imageInfo) {
        if (imageInfo != null) {
            view.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
        }
    }


    public void Update(PostDataSet postDataSet){
        if(UserID != firebaseUser.getUid()){
            Map<String, Object> viewcount = new HashMap<String, Object>();
            viewcount.put("postViewCount", String.valueOf(Integer.parseInt(postDataSet.getPostViewCount()) + 1));
            databaseReference.child(Key).updateChildren(viewcount);
        }

    }
    public void getProfile(final String Option){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("MyWorld").child("User").child(UserID);
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
    }
    public void DeletePost(){
        if(UserID.equals(firebaseUser.getUid())){
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowpostActivity.this);
            builder.setTitle("글 삭제");
            builder.setMessage("정말로 글을 삭제하시겠습니까?\n삭제된 글을 복구가 불가능합니다");
            builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    databaseReference.child(Key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ShowpostActivity.this, "글삭제에 성공했습니다", Toast.LENGTH_LONG).show();
                            getProfile("UpdatePostCount");
                            Intent intent = new Intent(ShowpostActivity.this, MainActivity.class);
                            intent.putExtra("로그인로그", "showpost");
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ShowpostActivity.this, "글삭제에 실패했습니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();
        }else{
            Toast.makeText(ShowpostActivity.this, "본인이 작성한 글만 삭제 하실 수 있습니다", Toast.LENGTH_LONG).show();
        }
    }
    public void DeleteImage(){
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        storageReference.child("PostImage").child(ImageName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DeletePost();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ShowpostActivity.this, "이미지 삭제에 실패했습니다", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void UpdateProfile(ProfileDataSet profileDataSet, String Option){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("MyWorld").child("User").child(UserID);
        Map<String, Object> update = new HashMap<>();
        if(Option.equals("UpdatePostViewCount")){
            update.put("allPostViewCount", String.valueOf(Integer.parseInt(profileDataSet.getAllPostViewCount()) + 1));
        }else if(Option.equals("UpdatePostCount")){
            update.put("mypostCount", String.valueOf(Integer.parseInt(profileDataSet.getMypostCount()) - 1));
        }
        databaseReference.updateChildren(update);
    }
}
