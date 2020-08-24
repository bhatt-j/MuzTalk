package com.example.muztalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().hide();

        final Button a_myRoom = (Button) findViewById(R.id.a_myRoom);
        a_myRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openRoomActivity();
            }


        });
    }
    public void openRoomActivity(){
        Intent intent = new Intent(this,RoomActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    public void finish(){
        super.finish();
       // overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

}