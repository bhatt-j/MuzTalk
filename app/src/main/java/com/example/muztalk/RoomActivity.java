package com.example.muztalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class RoomActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

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


}