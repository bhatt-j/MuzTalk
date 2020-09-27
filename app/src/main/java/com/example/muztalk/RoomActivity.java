package com.example.muztalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RoomActivity extends AppCompatActivity {
    SharedPreff sharedPreff;
    String userid;
    ImageView ROOM_CAMERA;
    TextView ROOM_NAME;
    FragmentPagerAdapter adapterViewPager;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    DatabaseReference FB_user;
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
        setContentView(R.layout.activity_room);
        getSupportActionBar().hide();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());


        Toast locate = Toast.makeText(RoomActivity.this,"Your location",Toast.LENGTH_SHORT);
        locate.setGravity(Gravity.CENTER | Gravity.START,90,40);
        locate.show();

        Toast yt = Toast.makeText(RoomActivity.this,"TV",Toast.LENGTH_SHORT);
        yt.setGravity(Gravity.CENTER | Gravity.START,90,60);
        yt.show();

        Toast chat = Toast.makeText(RoomActivity.this,"Chat here",Toast.LENGTH_SHORT);
        chat.setGravity(Gravity.CENTER | Gravity.START,90,0);
        chat.show();





        ROOM_NAME = findViewById(R.id.room_name);
        firebaseAuth = FirebaseAuth.getInstance();
        userid = firebaseAuth.getCurrentUser().getUid();
        FB_user = FirebaseDatabase.getInstance().getReference().child("users");
        FB_user.child(userid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String get_username_FB = dataSnapshot.child("username").getValue(String.class);

                        String name = get_username_FB + "'s Room";

                        ROOM_NAME.setText(name);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


        final ImageView a_speakers = (ImageView) findViewById(R.id.a_speakers);
        a_speakers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openMusicActivity();
            }


        });

        final ImageView a_globe = (ImageView) findViewById(R.id.a_globe);
        a_globe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openLocationActivity();
            }


        });


        final ImageView a_youtube = (ImageView) findViewById(R.id.a_youtube);
        a_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getPackageManager().getLaunchIntentForPackage("com.google.android.youtube");
                startActivity(i);
            }
        });


        ROOM_CAMERA=findViewById(R.id.room_camera);
        ROOM_CAMERA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencamera();
            }
        });

    }

    private void opencamera() {
        Intent intent = new Intent(this,CameraActivity.class);
        startActivity(intent);
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



    public void openMusicActivity(){
        Intent intent = new Intent(this,MusicActivity.class);
        startActivity(intent);
    }

    public void openLocationActivity(){
        Intent intent = new Intent(this,LocationActivity.class);
        startActivity(intent);
    }

    int counter = 0;
    @Override
    public void onBackPressed()
    {
        counter++;
        if(counter==1)
        {
            Intent intent = new Intent(this,MenuActivity.class);
            startActivity(intent);
        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:

            }
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }


}

//DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTimestamp()