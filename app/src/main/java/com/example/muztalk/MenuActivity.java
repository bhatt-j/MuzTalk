package com.example.muztalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    SharedPreff sharedPreff;

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



        final Button a_myRoom = (Button) findViewById(R.id.a_myRoom);
        a_myRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openRoomActivity();
            }


        });

        final ImageView a_user_profile = (ImageView) findViewById(R.id.a_user_profile);
        a_user_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { openUser_profileActivity();
            }


        });
    }
    public void openRoomActivity(){
        Intent intent = new Intent(this,RoomActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
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
    public void finish(){
        super.finish();
    }
    public void openUser_profileActivity(){
        Intent intent = new Intent(this,UserprofileActivity.class);
        startActivity(intent);
    }
}