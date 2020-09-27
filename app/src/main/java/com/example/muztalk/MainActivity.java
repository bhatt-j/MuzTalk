package com.example.muztalk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser auth_user;
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
        auth_user = firebaseAuth.getCurrentUser();
        if(auth_user != null )
        {
           // startActivity(new Intent(this,MenuActivity.class));
            //finish();
            int SPLASH_TIME_OUT = 3000;
            new Handler().postDelayed (new Runnable(){
                @Override
                public void run() {
                    Intent homeIntent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(homeIntent);

                    finish();
                }
            }, SPLASH_TIME_OUT);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        int SPLASH_TIME_OUT = 3000;
        new Handler().postDelayed (new Runnable(){
            @Override
            public void run() {
                Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(homeIntent);

                finish();
            }
        }, SPLASH_TIME_OUT);


    }
}
