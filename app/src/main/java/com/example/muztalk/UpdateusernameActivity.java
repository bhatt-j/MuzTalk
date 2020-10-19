package com.example.muztalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateusernameActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase db;
    Task<Void> databaseReference;
    FirebaseUser firebaseUser;
    EditText U_USERNAME;
    Button SUBMIT;
    SharedPreff sharedPreff;
    private DatabaseReference Reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreff = new SharedPreff(this);
        if(sharedPreff.loadNightModeState())
        {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateusername);
        getSupportActionBar().hide();

        U_USERNAME = (EditText) findViewById(R.id.a_username);
        SUBMIT = (Button) findViewById(R.id.UU_submitBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SUBMIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = U_USERNAME.getText().toString().trim();

                Reference =FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("username", username);
                hashMap.put("search", username.toLowerCase());
                Reference.updateChildren(hashMap).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UpdateusernameActivity.this, "Username Updated.",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),SettinglistActivity.class));
                            }
                        });

            }
        });

    }

}