package com.example.muztalk;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SigninActivity extends AppCompatActivity {

    EditText EmAIL,PASSWORD,CONFIRM_PASSWORD,USERNAME,DOB;
    Button SIGN_UP;
    Context mContext;

    Task<Void> databaseReference;
    FirebaseAuth firebaseAuth;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        Objects.requireNonNull(getSupportActionBar()).hide();

        EmAIL=findViewById(R.id.v_emailid);
        PASSWORD=findViewById(R.id.v_password);
        CONFIRM_PASSWORD=findViewById(R.id.v_confrim_password);
        USERNAME=findViewById(R.id.v_username);
        DOB=findViewById(R.id.a_DOB);
        SIGN_UP=findViewById(R.id.a_SignUp_button);

        firebaseAuth = FirebaseAuth.getInstance();

        SIGN_UP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmAIL.getText().toString().trim();
                String pass = PASSWORD.getText().toString().trim();
                final String UserName = USERNAME.getText().toString().trim();
                String c_pass = CONFIRM_PASSWORD.getText().toString().trim();
//                final String DoB = DOB.getText().toString().trim();

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
                if(TextUtils.isEmpty(c_pass))
                {
                    Toast.makeText(SigninActivity.this, "Please CONFIRM your password",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(pass.length()<6)
                {
                    Toast.makeText(SigninActivity.this, "Too short PASSWORD",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(pass.equals(c_pass))
                {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                        final users information = new users(UserName);
                                        databaseReference = FirebaseDatabase.getInstance().getReference("users")
                                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                                .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                        Toast.makeText(SigninActivity.this, "SIGN-UP COMPLETED.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                        mContext = getApplicationContext();
                                        Resources mResources = getResources();

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                                        builder.setSmallIcon(R.drawable.logo);
                                        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.logo);
                                        builder.setLargeIcon(bitmap);
                                        builder.setContentTitle("Sign-in Successful");
                                        builder.setContentText("Welcome to MuzTalk.");
                                        int notificationId = 1;
                                        Intent intent = new Intent(SigninActivity.this,MenuActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                        {
                                            String channelId = "1";
                                            NotificationChannel channel = new NotificationChannel(
                                                    channelId,
                                                    "Channel human readable title",
                                                    NotificationManager.IMPORTANCE_HIGH);
                                            assert manager != null;
                                            manager.createNotificationChannel(channel);
                                            builder.setChannelId(channelId);
                                        }
                                         assert manager != null;
                                         manager.notify(notificationId, builder.build());

                                        FirebaseUser user = firebaseAuth.getCurrentUser();


                                         assert user != null;
                                         user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SigninActivity.this, "Verification email has been sent.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(SigninActivity.this, "email can't sent.",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });


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


    /*final users information = new users(UserName);
                                        databaseReference = FirebaseDatabase.getInstance().getReference("users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
@Override
public void onComplete(@NonNull Task<Void> task) {
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        Toast.makeText(SigninActivity.this, "SIGN-UP COMPLETED.",
        Toast.LENGTH_SHORT).show();
        }
        });*/