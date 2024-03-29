package com.example.muztalk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muztalk.Adapters.AdapterChat;
import com.example.muztalk.Models.ModelChat;
import com.example.muztalk.Models.ModelUsers;
import com.example.muztalk.notifications.APIServices;
import com.example.muztalk.notifications.Client;
import com.example.muztalk.notifications.Data;
import com.example.muztalk.notifications.Response;
import com.example.muztalk.notifications.Sender;
import com.example.muztalk.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    SharedPreff sharedPreff;
    CircleImageView PROFILE_IMAGE;
    TextView username,STATUS;
    ImageButton SEND_BUTTON,SPEECH_TO_TEXT;
    EditText MSG;
    Toolbar TOOLBAR;
    //firebase
    DatabaseReference FB_user;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDBRef;
    //string
    String userid,hisUID,myUID,hisImage;
    //Api
    APIServices apiServices;
    boolean notify = false;

    List<ModelChat> chatList;
    AdapterChat adapterChat;
    RecyclerView recyclerView;

    Key publicKey = null;
    Key privateKey = null;

    private static final int REQUEST_CODE_PERMISSION = 1;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreff = new SharedPreff(this);
        if(sharedPreff.loadNightModeState())
        {
            setTheme(R.style.DarkTheme);
        }
        else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Objects.requireNonNull(getSupportActionBar()).hide();
        init();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //////////////////////////////Create api services
        apiServices = Client.getRetrofit("https://fcm.googleapis.com/").create(APIServices.class);

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDBRef = firebaseDatabase.getReference("users");

        Query userQuery = userDBRef.orderByChild("id").equalTo(hisUID);
            //////////////////////////////////////////////////////////////////////////////////////USERIMAGE AND USERNAME
        userQuery.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    /////////////////////////////////////////////////get_data
                    String name = ""+ds.child("username").getValue();
                    String image = ""+ds.child("imageURL").getValue();

                    /////////////////////////////////////////////////set_data
                    username.setText(name);
                    /*try {
                        ////////////////////////////////////////////////retrieve image
                        Picasso.get().load(image).into(profile_image);
                    }
                    catch (Exception e)
                    {
                        //exeption
                    }*/

                    String hisStatus = ""+ds.child("status").getValue();
                    String typingStatus = ""+ds.child("Typingto").getValue();

                    if(typingStatus.equals(myUID))
                    {
                       STATUS.setVisibility(View.VISIBLE);
                       STATUS.setText("Typing...");
                    }
                    else
                    {
                        if(hisStatus.equals("Active"))
                        {
                            STATUS.setVisibility(View.VISIBLE);
                            STATUS.setText("Active");
                            //PROFILE_IMAGE.setBorderWidth(5);
                            //PROFILE_IMAGE.setBorderColor(R.attr.online);
                        }
                        else
                        {
                            STATUS.setVisibility(View.VISIBLE);
                            STATUS.setText("Inactive");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
            ///////////////////////////////////////////////////////////////////////////////////////////////////CHECK TYPING
            MSG.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().trim().length() == 0)
                    {
                        fun_typing("None");
                    }
                    else
                    {
                        fun_typing(hisUID);
                    }
                }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
            ///////////////////////////////////////////////////////////////////////////////////////////////////SEND MESSAGE
            SEND_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;

                String MESSAGE = MSG.getText().toString().trim();
                if(TextUtils.isEmpty(MESSAGE))
                {
                    //empty msg
                    Toast.makeText(ChatActivity.this,"empty message",Toast.LENGTH_LONG).show();
                }
                else
                {
                    // Generate key pair for 2048-bit RSA encryption and decryption
                    /*try {
                        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                        kpg.initialize(2048);
                        KeyPair kp = kpg.genKeyPair();
                        publicKey = kp.getPublic();
                        privateKey = kp.getPrivate();
                        Log.d("Encoded string: ", String.valueOf(publicKey));
                    } catch (Exception e) {
                        Log.e("Crypto", "RSA key pair error");
                    }
                    ////////////////////////////////////////////////////////////////////////// Encode the original data with the RSA private key
                    byte[] encodedBytes = null;

                    try {
                        Cipher c = Cipher.getInstance("RSA");
                        c.init(Cipher.ENCRYPT_MODE, privateKey);
                        encodedBytes = c.doFinal(MESSAGE.getBytes());
                    } catch (Exception e)
                    {
                        Log.e("Crypto", "RSA encryption error");
                    }
                    String Enc_msg = new String(Base64.encodeToString(encodedBytes,Base64.DEFAULT));
                    assert encodedBytes != null;*/
                    SendMessage(MESSAGE);

                    /*   try {
                        Cipher c = Cipher.getInstance("RSA");
                        c.init(Cipher.ENCRYPT_MODE, privateKey);
                        encodedBytes = c.doFinal(MESSAGE.getBytes());
                    } catch (Exception e) {
                        Log.e("Crypto", "RSA encryption error");
                    }
                    Log.d("Encoded string: ", new String(Base64.encodeToString(encodedBytes, Base64.DEFAULT)));
                    byte[] decodedBytes = null;
                    try {
                        Cipher c = Cipher.getInstance("RSA");
                        c.init(Cipher.DECRYPT_MODE, publicKey);
                        decodedBytes = c.doFinal(encodedBytes);
                    } catch (Exception e) {
                        Log.e("Crypto", "RSA decryption error");
                    }
                    Log.d("Decoded string: ", new String(decodedBytes));
                */
                }
                MSG.setText("");
            }
        });
            readMessages(publicKey);
            SeenMessages();
            ///////////////////////////////////////////////////////////////////////////////////////////////////SPEECH_TO_TEXT
            SPEECH_TO_TEXT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetSpeech_fun();
                }
            });
    }


    //****************************************************************************************************************//
    private void SeenMessages() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUID) && chat.getSender().equals(hisUID))
                    {
                        HashMap<String, Object> hasSeenHashMap = new HashMap<>();
                        hasSeenHashMap.put("isseen",true);
                        ds.getRef().updateChildren(hasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }//////
    private void readMessages(Key publicKey) {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUID) && chat.getSender().equals(hisUID) ||
                            chat.getReceiver().equals(hisUID) && chat.getSender().equals(myUID))
                    {
                        chatList.add(chat);
                    }
                    adapterChat = new AdapterChat(ChatActivity.this, chatList,hisImage,publicKey);
                    adapterChat.notifyDataSetChanged();
                    /////////////////////////////////////////////////////////////////////////////////////////////////set adapter to recycler view
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }///////
    private void SendMessage(String Message) {
        DatabaseReference df1 = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String ,Object> hashM = new HashMap<>();
        hashM.put("sender",myUID);
        hashM.put("receiver",hisUID);
        hashM.put("message",Message);
        hashM.put("timestamp",timestamp);
        hashM.put("isseen",false);
        df1.child("Chats").push().setValue(hashM);

        //MSG.setText("");

        String msg = Message;
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(myUID);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUsers user = snapshot.getValue(ModelUsers.class);
                if(notify)
                {
                    //sendNotification(hisUID, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ///////////////////////////////////////////////////////////////////CHAT LIST
        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(myUID)
                .child(hisUID);
        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    chatRef1.child("id").setValue(hisUID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(hisUID)
                .child(myUID);
        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    chatRef2.child("id").setValue(myUID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }//////
    private void sendNotification(String hisUID, String username, String message) {
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(hisUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUID , username+":"+message, "New Message",hisUID ,R.drawable.logo);

                    Sender sender = new Sender(data, token.getToken());
                    apiServices.sendNotification(sender)
                            .enqueue(new Callback<Response>() {
                                @Override
                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                    Toast.makeText(ChatActivity.this,""+response.message(),Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onFailure(Call<Response> call, Throwable t) {}
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }//////
    public void init() {
        PROFILE_IMAGE = findViewById(R.id.profile_image);
        username = findViewById(R.id.username_chat);
        STATUS = findViewById(R.id.status);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FB_user = FirebaseDatabase.getInstance().getReference().child("users");
        userid = firebaseAuth.getCurrentUser().getUid();
        TOOLBAR = findViewById(R.id.chat_toolbar);
        MSG = findViewById(R.id.message);
        SEND_BUTTON = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.chat_recycler);
        SPEECH_TO_TEXT = findViewById(R.id.speech_to_text);

    }//////
    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null)
        {
            myUID = user.getUid();
        }
    }////
    public void openCamera(View view) {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ChatActivity.this,new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },REQUEST_CODE_PERMISSION);
        }
        else
        {
            Intent intent = new Intent(this,CameraActivity.class);
            startActivity(intent);
        }
    }//////
    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }/////
    private void fun_typing(String typing){
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Typingto", typing);
        reference.updateChildren(hashMap);
    }//////
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }/////
    @Override
    protected void onResume() {
        super.onResume();
        status("Active");
    }//////
    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
        status("Inactive");
        fun_typing("None");
    }//////

    private void GetSpeech_fun() {
        Intent speech_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speech_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speech_intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if(speech_intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(speech_intent,10);
        }
        else
        {
            Toast.makeText(this,"Device do not support Speech Input",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case 10:
                if(resultCode == RESULT_OK && data != null)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    assert result != null;
                    SendMessage(result.get(0));
                }
        }
    }
}








