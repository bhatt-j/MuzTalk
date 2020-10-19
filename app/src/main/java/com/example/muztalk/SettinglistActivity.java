package com.example.muztalk;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettinglistActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    //FirebaseFirestore fstore;
   // ListView listView;
    FirebaseUser user;
    SharedPreff sharedPreff;
    final String MYPREF = "nightmodepref";
    final String KEY_ISNIGHTMODE = "isNightMode";
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    SwitchCompat NOTIFY_SWITCH , T_SWITCH;
    //String[] s1={"Update Username","Notifications","Theme","invite friend","Change Password","Log-out","Delete Account"};
    //int[] icons={R.drawable.user_icon1,R.drawable.notifications_icon,R.drawable.theme_icon,R.drawable.usergroup_icon,R.drawable.password,R.drawable.exit_icon,R.drawable.delete_icon};
    Button UPDATE_USERNAME, INVITE_FRIEND,CHANGE_password,LOG_OUT,DELETE_ACC;

    String EM_MESSAGE = "Hey,\n" +
            "Muztalk is an entertaining app that I use to \n" +
            "message and enjoy videos with my people.\n" +
            "\n" +
            "Give a try to this app.\n" +
            "Thanks.";
    String EM_SUBJECT = "Muztalk : Android Phone";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreff = new SharedPreff(this);
        if(sharedPreff.loadNightModeState() || AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settinglist);
        getSupportActionBar().hide();

        init();

        UPDATE_USERNAME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { fun_updateUsername();                                              //update_username
            }
        });
        LOG_OUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { logout();                                                                 //log_out
            }
        });
        INVITE_FRIEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                                                                     //invite_friend
               /* Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT,EM_SUBJECT);
                intent.putExtra(Intent.EXTRA_TEXT,EM_MESSAGE);
                startActivity(intent);*/

                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    //shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    intent.putExtra(Intent.EXTRA_SUBJECT,EM_SUBJECT);
                    //shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    intent.putExtra(Intent.EXTRA_TEXT, EM_MESSAGE);
                    startActivity(Intent.createChooser(intent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }

            }
        });
        if(sharedPreff.loadNightModeState() || AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        {
            T_SWITCH.setChecked(true);
        }
        if(T_SWITCH!=null) {
            T_SWITCH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        sharedPreff.setNightModeState(true);
                        restartApp();
                    } else {
                        sharedPreff.setNightModeState(false);
                        restartApp();
                    }
                }

            });
        }
        CHANGE_password.setOnClickListener(new View.OnClickListener() {                           //////////////////////////change_pass
            @Override
            public void onClick(View v) {
                final EditText resetpass = new EditText(v.getContext());
                final AlertDialog.Builder pass_change_dialog = new AlertDialog.Builder(v.getContext());
                pass_change_dialog.setTitle("Change Password?");
                pass_change_dialog.setMessage("Enter new password more than 6 characters.");
                pass_change_dialog.setView(resetpass);

                pass_change_dialog.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newpass = resetpass.getText().toString();
                                user.updatePassword(newpass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SettinglistActivity.this,"Password changed successfully.",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SettinglistActivity.this,"Password cannot change.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                pass_change_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog =pass_change_dialog.create();
                dialog.show();
            }
        });
        DELETE_ACC.setOnClickListener(new View.OnClickListener() {                                                //delete_acc
            @Override
            public void onClick(View v) {
                final EditText pass = new EditText(v.getContext());
                final AlertDialog.Builder pass_change_dialog = new AlertDialog.Builder(v.getContext());
                pass_change_dialog.setTitle("Delete Account?");
                pass_change_dialog.setMessage("Enter your password.");
                pass_change_dialog.setView(pass);


                pass_change_dialog.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cpass = pass.getText().toString();
                                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(),cpass);
                                user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    String userid = user.getUid();
                                                    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Chats").child(userid);
                                                    dRef.removeValue();
                                                    firebaseAuth.signOut();
                                                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                                    Toast.makeText(SettinglistActivity.this,"Account deleted successfully.",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SettinglistActivity.this,"Cannot delete account.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                pass_change_dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog =pass_change_dialog.create();
                dialog.show();

            }
        });


    }

    private void init() {

        UPDATE_USERNAME = findViewById(R.id.a_update_username);
        INVITE_FRIEND = findViewById(R.id.a_invite_friend);
        CHANGE_password=findViewById(R.id.a_change_password);
        LOG_OUT=findViewById(R.id.a_log_out);
        DELETE_ACC = findViewById(R.id.a_delete_acc);
        NOTIFY_SWITCH = findViewById(R.id.switch1);
        T_SWITCH = findViewById(R.id.switch2);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

    }/////////////
    int counter = 0;
    @Override
    public void onBackPressed()
    {
        counter++;
        if(counter==1)
        {
            Intent intent = new Intent(this,UserprofileActivity.class);
            startActivity(intent);
        }
    }/////////////////////

    public void fun_updateUsername(){
        Intent intent = new Intent(this,UpdateusernameActivity.class);
        startActivity(intent);
    }/////////////
    public void logout()
    {
        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));

    }/////////////////

    private void restartApp() {
        Intent i = new Intent(getApplicationContext(), SettinglistActivity.class);
        startActivity(i);
        finish();
    }/////////////////


}

/*


 */