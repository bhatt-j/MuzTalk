package com.example.muztalk;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    SharedPreff sharedPreff;
    CircleImageView profile_image;
    TextView username;
    DatabaseReference FB_user;
    FirebaseAuth firebaseAuth;
    String userid;
    FirebaseUser firebaseUser;
    ImageButton SEND_BUTTON;
    EditText MSG;
    Toolbar TOOLBAR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreff = new SharedPreff(this);
        if(sharedPreff.loadNightModeState())
        {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        setSupportActionBar(TOOLBAR);
        TOOLBAR.setTitle("");

                        username.setText("");
    }
    public void init()
    {
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username_chat);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FB_user = FirebaseDatabase.getInstance().getReference().child("users");
        userid = firebaseAuth.getCurrentUser().getUid();
        TOOLBAR = findViewById(R.id.chat_toolbar);
        MSG = findViewById(R.id.message);
        SEND_BUTTON = findViewById(R.id.btn_send);
    }
}