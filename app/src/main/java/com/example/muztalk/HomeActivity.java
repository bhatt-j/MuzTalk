package com.example.muztalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    EditText LPASSWORD,LEMAIL;
    Button LOGIN;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LPASSWORD = (EditText)findViewById(R.id.v_passwordlogin);
        LEMAIL = (EditText)findViewById(R.id.v_emaillogin);
        LOGIN = (Button)findViewById(R.id.a_loginbutton);

        firebaseAuth = FirebaseAuth.getInstance();

        LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String lemail = LEMAIL.getText().toString().trim();
                String lpass = LPASSWORD.getText().toString().trim();
                if (TextUtils.isEmpty(lemail)) {
                    Toast.makeText(HomeActivity.this, "Please Enter your EMAIL-ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(lpass)) {
                    Toast.makeText(HomeActivity.this, "Please Enter your PASSWORD", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (lpass.length() < 6) {
                    Toast.makeText(HomeActivity.this, "Invalid PASSWORD", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(lemail, lpass)
                        .addOnCompleteListener(HomeActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(),MenuActivity.class));
                                    Toast.makeText(HomeActivity.this, "Logged-in successfully", Toast.LENGTH_SHORT).show();
                                } else {

                                }

                                // ...
                            }
                        });


            }
        });

        final Button a_newuser = (Button) findViewById(R.id.a_newuser);
        a_newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openSigninActivity();
            }


        });

    }
    public void openSigninActivity(){
        Intent intent = new Intent(this,SigninActivity.class);
        startActivity(intent);
    }






}

