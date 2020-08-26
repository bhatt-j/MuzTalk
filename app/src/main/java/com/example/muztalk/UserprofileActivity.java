package com.example.muztalk;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserprofileActivity extends AppCompatActivity implements onActivityResult1 {
    TextView useremail,username;
    ImageView profilepic;
    Task<Void> databaseReference;
    public Uri imageUri;
    private StorageReference storageReference;
    private ConstraintLayout constraintLayout;
    StorageReference riversRef;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final int REQUEST_CODE_CAPTURE_IMAGE=2;

    public String currentImagePath;
    private ImageView captureImage;

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
        captureImage = findViewById(R.id.profilepic);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        useremail = (TextView) findViewById(R.id.useremail);
        username = (TextView) findViewById(R.id.username);
        profilepic = (ImageView) findViewById(R.id.profilepic);

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserprofileActivity.this);
                builder.setTitle("Choose Profile Pic");
                builder.setPositiveButton("Camera", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //finish();
                        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(UserprofileActivity.this,new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },REQUEST_CODE_PERMISSION);
                        }
                        else
                        {
                            dispatchcameraintent();
                        }
                    }
                });
                builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choosePic();
                    }
                });
                AlertDialog dialog =builder.create();
                dialog.show();
            }
        });

        final ImageView a_settings = (ImageView) findViewById(R.id.a_settings);
        a_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openSettingsActivity();
            }
        });
    }
    private void dispatchcameraintent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            File imageFile = null;
            try{
                imageFile = createImageFile();
            }
            catch (IOException exception){
                Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT).show();
            }
            if(imageFile!=null)
            {
                FileProvider fileProvider = new FileProvider();
                Uri imageUri = fileProvider.getUriForFile(this,"com.example.muztalk.fileprovider",imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,REQUEST_CODE_CAPTURE_IMAGE);
            }
        }
    }

    private File createImageFile() throws IOException
    {
        String file_Name = "IMAGE_"+ new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(new Date());

        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(file_Name,".jpg",directory);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                dispatchcameraintent();
            }
            else
            {
                Toast.makeText(this, "Not all permission granted.",Toast.LENGTH_SHORT).show();
            }
        }
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

    public Bitmap getScaledBitmap(ImageView imageView)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int scaleFactor = Math.min(
                options.outWidth/imageView.getWidth(),
                 options.outHeight/imageView.getHeight()
        );
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        return BitmapFactory.decodeFile(currentImagePath,options);
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

        if(requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK)
        {
            try{
                //captureImage.setImageBitmap(getScaledBitmap(captureImage));
                captureImage.setImageBitmap(BitmapFactory.decodeFile(currentImagePath));
                Uri file = Uri.fromFile(new File(currentImagePath));
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                riversRef = storageReference.child("Profileimages/"+uid);

                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                               // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Snackbar.make(findViewById(android.R.id.content),"Profile pic uploaded",Snackbar.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                Snackbar.make(findViewById(android.R.id.content),"Cannot Profile pic uploaded",Snackbar.LENGTH_LONG).show();
                            }
                        });

            }
            catch(Exception ex)
            {
                Toast.makeText(this,ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}




//  profilepic.setOnClickListener(new View.OnClickListener() {
//    @Override
//  public void onClick(View v) {
//    choosePic();
                /*Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);*/
//}
//});