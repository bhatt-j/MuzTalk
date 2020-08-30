package com.example.muztalk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SigninActivity extends AppCompatActivity {

    EditText EmAIL,PASSWORD,CONFIRMPASSWORD,USERNAME;
    Button SIGNUP;

    Task<Void> databaseReference;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getSupportActionBar().hide();

        EmAIL=(EditText)findViewById(R.id.v_emailid);
        PASSWORD=(EditText)findViewById(R.id.v_password);
        CONFIRMPASSWORD=(EditText)findViewById(R.id.v_confrim_password);
        USERNAME=(EditText)findViewById(R.id.v_username);
        SIGNUP=(Button)findViewById(R.id.a_SignUp_button);

        firebaseAuth = FirebaseAuth.getInstance();
//        databaseReference = firebaseDatabase.getInstance().getReference("Accounts");

        SIGNUP.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                        String email = EmAIL.getText().toString().trim();
                        String pass = PASSWORD.getText().toString().trim();
                        final String UserName = USERNAME.getText().toString().trim();
                        String cpass = CONFIRMPASSWORD.getText().toString().trim();

                        if(TextUtils.isEmpty(email))
                        {
                            Toast.makeText(SigninActivity.this, "Please Enter your EMAIL-ID",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(TextUtils.isEmpty(pass))
                        {
                            Toast.makeText(SigninActivity.this, "Please Enter your PASSWORD",Toast.LENGTH_SHORT).show();
                            return;
                        }
                if(TextUtils.isEmpty(cpass))
                {
                    Toast.makeText(SigninActivity.this, "Please CONFIRM your password",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(pass.length()<6)
                {
                    Toast.makeText(SigninActivity.this, "Too short PASSWORD",Toast.LENGTH_SHORT).show();
                    return;
                }

              if(pass.equals(cpass))
                {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        final users information = new users(UserName);
                                        databaseReference = FirebaseDatabase.getInstance().getReference("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                                        Toast.makeText(SigninActivity.this, "SIGN-UP COMPLETED.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                    else {

                                        Toast.makeText(SigninActivity.this, "SIGN-UP FAILED.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
                else
              {
                  Toast.makeText(SigninActivity.this, "Cannot match password",
                          Toast.LENGTH_SHORT).show();
              }


                }
        });


    }
}