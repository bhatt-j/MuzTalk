package com.example.muztalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        final Button a_myroombutton = (Button) findViewById(R.id.a_myroombutton);
        a_myroombutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openRoomActivity();
            }


        });

    }
    public void openRoomActivity(){
        Intent intent = new Intent(this,RoomActivity.class);
        startActivity(intent);
    }

}