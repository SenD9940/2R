package com.fbf.a2r;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.view.View.*;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private RelativeLayout signInButton, Button_GuestLogin, Button_AdminLogin;
    private ImageView icon_google;
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ArrayList<String> keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        }

        if (Build.VERSION.SDK_INT >= 21) {
        } else if (Build.VERSION.SDK_INT < 21) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        signInButton = findViewById(R.id.signInButton);
        Button_GuestLogin = findViewById(R.id.Button_AnanymusLogin);
        Button_AdminLogin = findViewById(R.id.Button_LoginAdmin);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        view.getSystemUiVisibility();
        getWindow().setStatusBarColor(Color.parseColor("#ffffff"));
        mAuth = FirebaseAuth.getInstance();
        isExsitProfile();

        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplication(), MainActivity.class);
            Toast.makeText(this, "자동로그인 되었습니다", Toast.LENGTH_SHORT).show();
            intent.putExtra("로그인로그", "googleLogin");
            startActivity(intent);
            finish();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "로그인에 성공 했습니다", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "로그인에 실패했습니다\n네트워크를 확인해 주세요", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    public void setProfile() {
        if(!this.keys.contains(mAuth.getUid())){
            databaseReference = firebaseDatabase.getReference("MyWorld").child("User").child(mAuth.getUid());
            ProfileDataSet profileDataSet = new ProfileDataSet(mAuth.getCurrentUser().getDisplayName(), mAuth.getCurrentUser().getPhotoUrl().toString(), "0", "0", mAuth.getUid());
            databaseReference.setValue(profileDataSet);
        }
    }

    public void isExsitProfile() {
        this.keys = new ArrayList<String>();
        databaseReference = firebaseDatabase.getReference("MyWorld").child("User");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("uid", dataSnapshot.getKey());
                addkey(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            setProfile();
            Toast.makeText(getApplicationContext(), "환영합니다^^", Toast.LENGTH_SHORT).show();
            intent.putExtra("로그인로그", "googleLogin");
            startActivity(intent);
            finish();
        }
    }

    public void ChooseCategory(){

    }

    public void onclick(View view) {
        switch (view.getId()){
            case R.id.Button_AnanymusLogin:
                Intent Guest_login = new Intent(LoginActivity.this, MainActivity.class);
                Guest_login.putExtra("로그인로그", "guestlogin");
                Toast.makeText(getApplicationContext(), "게스트로 로그인 되었습니다", Toast.LENGTH_SHORT).show();
                startActivity(Guest_login);
                break;

            case R.id.Button_LoginAdmin:
                Intent Admin_login = new Intent(this, MainActivity.class);
                Admin_login.putExtra("로그인로그", "adminlogin");
                startActivity(Admin_login);
                break;
        }
    }

    private void addkey(String key){
        this.keys.add(key);
    }
}