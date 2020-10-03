package com.example.muztalk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserprofileActivity extends AppCompatActivity {
    ProgressBar PROGRESSBAR;
    File localfile;
    TextView USEREMAIL,USERNAME;
    ImageView profilepic;
    DatabaseReference FB_user;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userid;
    DrawerLayout drawerLayout;
    String get_username_FB;
    NavigationView navigationView;
    ImageView SETTINGS;
    Task<Void> databaseReference;
    public Uri imageUri;
    private StorageReference storageReference;
    StorageReference riversRef;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final int REQUEST_CODE_CAPTURE_IMAGE=2;

    public String currentImagePath;
    private ImageView captureImage;
    SharedPreff sharedPreff;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreff = new SharedPreff(this);
        if(sharedPreff.loadNightModeState() || AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        getSupportActionBar().hide();


        init();
        PROGRESSBAR.setVisibility(View.GONE);
        readRealTimeData();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String dp_name = get_username_FB +"_Propic" + uid ;
        String extension = ".jpeg";
        storageReference.child("Profileimages/"+dp_name+extension);

        try {
            localfile = File.createTempFile(dp_name,"jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        storageReference.getFile(localfile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap=BitmapFactory.decodeFile(localfile.getAbsolutePath());
                        profilepic.setImageBitmap(bitmap);

                    }
                });


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    get_set_propic();                                       //shows dialog box (gallery or camera)

            }
        });


        SETTINGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { openSettingsActivity();
            }
        });
    }

    int counter = 0;
    @Override
    public void onBackPressed()
    {
        counter++;
        if(counter==1)
        {
            Intent intent = new Intent(this,TotalchatsActivity.class);
            startActivity(intent);
        }
    }
    private void get_set_propic() {

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
                    dispatch_camera_intent();
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

    @SuppressLint("CutPasteId")
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        userid = firebaseAuth.getCurrentUser().getUid();
        USEREMAIL = findViewById(R.id.useremail);
        USERNAME = findViewById(R.id.username);
        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.nav_setting);
        SETTINGS = findViewById(R.id.a_settings);
       // toolbar = findViewById(R.id.toolbar_set);
        FB_user = FirebaseDatabase.getInstance().getReference().child("users");
        captureImage = findViewById(R.id.profilepic);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        profilepic = findViewById(R.id.profilepic);
        PROGRESSBAR = findViewById(R.id.image_progressBar);

    }

    private void readRealTimeData() {
        FB_user.child(userid)
                .addValueEventListener(new ValueEventListener() {



                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        get_username_FB = dataSnapshot.child("username").getValue(String.class);
                       // String get_dob_FB = dataSnapshot.child("Date Of Birth").getValue(String.class);

                        USERNAME.setText(get_username_FB);
                        USEREMAIL.setText(user.getEmail());
                        //USEREMAIL.setText(get_dob_FB);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void dispatch_camera_intent(){
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
                Uri imageUri = FileProvider.getUriForFile(this,"com.example.muztalk.fileprovider",imageFile);
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
                dispatch_camera_intent();
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
    @SuppressLint("WrongConstant")
    public void openSettingsActivity(){
      //  drawerLayout.openDrawer(Gravity.NO_GRAVITY);
        Intent intent = new Intent(this, SettinglistActivity.class);
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
                PROGRESSBAR.setVisibility(View.VISIBLE);

                assert data != null;
                Uri imageUri = data.getData();

                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String dp_name = get_username_FB +"_Propic" + uid ;
                riversRef = storageReference.child("Profileimages/"+dp_name);

                assert imageUri != null;
                riversRef.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Snackbar.make(findViewById(android.R.id.content),"Profile pic uploaded",Snackbar.LENGTH_LONG).show();
                                PROGRESSBAR.setVisibility(View.GONE);
                                profilepic.setImageURI(imageUri);
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
                PROGRESSBAR.setVisibility(View.VISIBLE);

                Uri file = Uri.fromFile(new File(currentImagePath));
                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String dp_name = get_username_FB +"_Propic" + uid ;
                riversRef = storageReference.child("Profileimages/"+dp_name);

                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                               // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Snackbar.make(findViewById(android.R.id.content),"Profile pic uploaded",Snackbar.LENGTH_LONG).show();
                                PROGRESSBAR.setVisibility(View.GONE);
                                captureImage.setImageBitmap(BitmapFactory.decodeFile(currentImagePath));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                Snackbar.make(findViewById(android.R.id.content),"Cannot Profile pic uploaded",Snackbar.LENGTH_LONG).show();
                                PROGRESSBAR.setVisibility(View.GONE);
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
