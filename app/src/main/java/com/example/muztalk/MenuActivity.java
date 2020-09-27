package com.example.muztalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {
    SharedPreff sharedPreff;
    ImageView PROFILE,CHATS;
    FirebaseUser firebaseUser;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreff = new SharedPreff(this);
        if(sharedPreff.loadNightModeState())
        {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();
        PROFILE = findViewById(R.id.a_user_profile);
        CHATS = findViewById(R.id.a_chat);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        CHATS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*UserlistFragment userlistFragment = new UserlistFragment();
                FragmentTransaction ft_user = getSupportFragmentManager().beginTransaction();
                ft_user.replace(R.id.container,userlistFragment,"");
                ft_user.commit();*/
                open_user();
            }
        });


        final Button a_myRoom = findViewById(R.id.a_myRoom);
        a_myRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                openRoomActivity();
            }
        });


        PROFILE.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) { openUser_profileActivity();
            }


        });
    }

    private void open_user()
    {
        Intent intent = new Intent(this,
                TotalchatsActivity.class);
        startActivity(intent);
    }
    public void openRoomActivity()
    {
        Intent intent = new Intent(this,RoomActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }


    private void status(String status){


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
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
            Toast.makeText(MenuActivity.this,"Cannot move back.",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void finish()
    {
        super.finish();
    }
    public void openUser_profileActivity()
    {
        Intent intent = new Intent(this,UserprofileActivity.class);
        startActivity(intent);
    }
}