package com.example.muztalk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UserprofileActivity extends AppCompatActivity {
    TextView useremail,username;
    ImageView profilepic;
    Task<Void> databaseReference;
    public Uri imageUri;
    private StorageReference storageReference;
    private ConstraintLayout constraintLayout;
    StorageReference riversRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        getSupportActionBar().hide();

        //constraintLayout = findViewById(R.id.layout);
        //ConstraintLayout constraintLayout = findViewById(R.id.layout);
        /*AnimationDrawable animationDrawable = (AnimationDrawable)constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();*/
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        useremail = (TextView) findViewById(R.id.useremail);
        username = (TextView) findViewById(R.id.username);
        profilepic = (ImageView) findViewById(R.id.profilepic);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePic();
                /*Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);*/
            }
        });

        final ImageView a_settings = (ImageView) findViewById(R.id.a_settings);
        a_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openSettingsActivity();
            }


        });


    }
    private void choosePic()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1000);
    }
    public void openSettingsActivity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1000)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                assert data != null;
                Uri imageUri = data.getData();
                profilepic.setImageURI(imageUri);
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                riversRef = storageReference.child("Profileimages/"+uid);

                assert imageUri != null;
                riversRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Snackbar.make(findViewById(android.R.id.content),"Profile pic uploaded",Snackbar.LENGTH_LONG).show();


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                Toast.makeText(UserprofileActivity.this, "Profile Picture cannot be Added.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        }
    }
}