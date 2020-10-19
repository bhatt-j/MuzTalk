package com.example.muztalk;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.muztalk.Fragments.ChatlistFragment;
import com.example.muztalk.Fragments.FriendslistFragment;
import com.example.muztalk.Fragments.UsersearchFragment;
import com.example.muztalk.notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class TotalchatsActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    ImageView CAM;
    SharedPreff sharedPreff;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    DatabaseReference FB_user;
    FirebaseAuth firebaseAuth;
    String userid;
    String mUID;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private int backpressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreff = new SharedPreff(this);
        if(sharedPreff.loadNightModeState())
        {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totalchats);
        Objects.requireNonNull(getSupportActionBar()).hide();
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FB_user = FirebaseDatabase.getInstance().getReference().child("users");
        userid = firebaseAuth.getCurrentUser().getUid();
        FB_user.child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String get_username_FB = dataSnapshot.child("username").getValue(String.class);
                        username.setText(get_username_FB);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        CAM = findViewById(R.id.face_camera);
        CAM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencamera();
            }
        });


        BottomNavigationView navigationView = findViewById(R.id.navigation_bar);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

///////////////////////////////////////////////////////////////////////////////////////////default fragment
        ChatlistFragment chatlistFragment = new ChatlistFragment();
        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
        ft2.replace(R.id.content,chatlistFragment,"");
        ft2.commit();

        checkUserStatus();
/////////////////////////////////////////////////////////////////////////////////////////update token
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void opencamera() {
        Intent intent = new Intent(this,FacecameraActivity.class);
        startActivity(intent);
    }

    private void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }

    public void open_camera_story(View view)
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(TotalchatsActivity.this,new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },REQUEST_CODE_PERMISSION);
        }
        else
        {
            Intent intent = new Intent(this,CameraActivity.class);
            startActivity(intent);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId())
                    {
                        case R.id.nav_users:
                            //fragment transaction
                            UsersearchFragment usersearchFragment = new UsersearchFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content,usersearchFragment,"");
                            ft1.commit();
                            return true;
                        case R.id.nav_chats:
                            //fragment transaction
                            ChatlistFragment chatlistFragment = new ChatlistFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,chatlistFragment,"");
                            ft2.commit();
                            return true;
                        case R.id.nav_friends:
                            //fragment transaction
                            FriendslistFragment friendslistFragment = new FriendslistFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content,friendslistFragment,"");
                            ft3.commit();
                            return true;
                        case R.id.profile:
                            //fragment transaction
                            Intent intent = new Intent(TotalchatsActivity.this,
                                    UserprofileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            return true;
                        case R.id.room:
                            //fragment transaction
                            Intent room_intent = new Intent(TotalchatsActivity.this,
                                    RoomActivity.class);
                            room_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(room_intent);
                            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                            return true;
                    }
                    return false;
                }
            };

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }
    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null)
        {
            mUID = user.getUid();
            ///////////////////////////////////////////////////////////////////////save uid in Shared Preferences
            SharedPreferences sp = getSharedPreferences("SP_users",MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID",mUID);
            editor.apply();
        }
    }
    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
        status("Active");
    }
    @Override
    protected void onPause() {
        super.onPause();
        status("Inactive");
    }
    int counter = 0;
    @Override
    public void onBackPressed()
    {
        if(counter==1)
        {
            Intent intent = new Intent(this,MenuActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(TotalchatsActivity.this,"Cannot move back.",Toast.LENGTH_SHORT).show();
        }
    }
}
