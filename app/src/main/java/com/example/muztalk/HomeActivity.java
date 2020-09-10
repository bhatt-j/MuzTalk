package com.example.muztalk;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static java.util.Objects.requireNonNull;

public class HomeActivity extends AppCompatActivity {

    EditText L_PASSWORD,L_EMAIL;
    TextView FORGOT_PASS;
    Button LOGIN;
    String mail;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        requireNonNull(getSupportActionBar()).hide();

        L_PASSWORD = findViewById(R.id.v_passwordlogin);
        L_EMAIL = findViewById(R.id.v_email_login);
        LOGIN = findViewById(R.id.a_loginbutton);
        FORGOT_PASS = findViewById(R.id.a_forget_password);
        firebaseAuth = FirebaseAuth.getInstance();



        LOGIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                login();
            }
        });

        //check user login or not
        /*FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();

        FirebaseUser auth_user = auth.getCurrentUser();
        if(auth_user != null )
        {
            //already logged-in
            startActivity(new Intent(this,MenuActivity.class));
            finish();
        }
        else
        {
            //go to login activity
            startActivity(new Intent(this,HomeActivity.class));
            finish();

        }*/


        final TextView a_new_user = findViewById(R.id.a_newuser);
        a_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { open_Sign_in_Activity();
            }


        });

        FORGOT_PASS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetmail = new EditText(v.getContext());
                final AlertDialog.Builder pass_reset_dialog = new AlertDialog.Builder(v.getContext());
                pass_reset_dialog.setTitle("Reset Password?");
                pass_reset_dialog.setMessage("Enter your email here.");
                pass_reset_dialog.setView(resetmail);


                pass_reset_dialog.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mail = resetmail.getText().toString();
                                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {

                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(HomeActivity.this,"Reset link sent to your email",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(HomeActivity.this,"Reset link cannot be sent to your email",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                pass_reset_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog =pass_reset_dialog.create();
                dialog.show();

            }


        });

    }

    int counter = 0;
    @Override
    public void onBackPressed()
    {
        if(counter==1)
        {
            finish();
        }
    }
    private void login() {
        String l_email = L_EMAIL.getText().toString().trim();
        String l_pass;
        l_pass = L_PASSWORD.getText().toString().trim();
        if (TextUtils.isEmpty(l_email)) {
            Toast.makeText(HomeActivity.this, "Please Enter your EMAIL-ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(l_pass)) {
            Toast.makeText(HomeActivity.this, "Please Enter your PASSWORD", Toast.LENGTH_SHORT).show();
            return;
        }
        if (l_pass.length() < 6) {
            Toast.makeText(HomeActivity.this, "Invalid PASSWORD", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(l_email, l_pass)
                .addOnCompleteListener(HomeActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(),MenuActivity.class));
                            Toast.makeText(HomeActivity.this, "Logged-in successfully", Toast.LENGTH_SHORT).show();
                                    /*rootNode = FirebaseDatabase.getInstance();
                                    reference= rootNode.getReference("username");
                                    reference.setValue("first data");*/
                        } else {
                            Toast.makeText(HomeActivity.this, "Log-in UnSuccessful.", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }

                        // ...
                    }
                });

    }

    public void open_Sign_in_Activity(){

        Intent intent = new Intent(this,SigninActivity.class);
        startActivity(intent);

        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }



}

