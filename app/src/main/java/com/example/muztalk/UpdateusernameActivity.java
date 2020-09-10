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
import com.google.firebase.database.FirebaseDatabase;

public class UpdateusernameActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase db;
    Task<Void> databaseReference;
    EditText U_USERNAME;
    Button SUBMIT;
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
        setContentView(R.layout.activity_updateusername);
        getSupportActionBar().hide();

        U_USERNAME = (EditText) findViewById(R.id.a_username);
        SUBMIT = (Button) findViewById(R.id.UU_submitBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        SUBMIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String FullName = U_USERNAME.getText().toString().trim();
                final users details = new users(FullName);

                databaseReference = FirebaseDatabase.getInstance().getReference("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
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