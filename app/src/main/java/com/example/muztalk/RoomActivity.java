package com.example.muztalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class RoomActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_room);
        getSupportActionBar().hide();

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



}