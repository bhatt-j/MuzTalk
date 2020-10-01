package com.example.muztalk;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muztalk.adapter.AdapterChat;
import com.example.muztalk.model.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.crypto.Cipher;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    SharedPreff sharedPreff;
    CircleImageView profile_image;
    TextView username;
    ImageButton SEND_BUTTON;
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

    List<ModelChat> chatList;
    AdapterChat adapterChat;
    RecyclerView recyclerView;


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

        Intent intent = getIntent();
        hisUID = intent.getStringExtra("hisUID");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDBRef = firebaseDatabase.getReference("users");

        Query userQuery = userDBRef.orderByChild("id").equalTo(hisUID);
        //user image and name
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    //get_data
                    String name = ""+ds.child("username").getValue();
                    String image = ""+ds.child("imageURL").getValue();

                    //set_data
                    username.setText(name);
                    /*try {
                    //retrieve image
                        Picasso.get().load(image).into(profile_image);
                    }
                    catch (Exception e)
                    {
                        //exeption
                    }*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SEND_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String MESSAGE = MSG.getText().toString().trim();
                if(TextUtils.isEmpty(MESSAGE))
                {
                    //empty msg
                    Toast.makeText(ChatActivity.this,"empty message",Toast.LENGTH_LONG).show();
                }
                else
                {
                    // Generate key pair for 2048-bit RSA encryption and decryption
                    Key publicKey = null;
                    Key privateKey = null;
                    try {
                        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                        kpg.initialize(2048);
                        KeyPair kp = kpg.genKeyPair();
                        publicKey = kp.getPublic();
                        privateKey = kp.getPrivate();
                    } catch (Exception e) {
                        Log.e("Crypto", "RSA key pair error");
                    }

// Encode the original data with the RSA private key
                    byte[] encodedBytes = null;
                    try {
                        Cipher c = Cipher.getInstance("RSA");
                        c.init(Cipher.ENCRYPT_MODE, privateKey);
                        encodedBytes = c.doFinal(MESSAGE.getBytes());
                    } catch (Exception e) {
                        Log.e("Crypto", "RSA encryption error");
                    }

                    Log.d("Encoded string: ", new String(Base64.encodeToString(encodedBytes, Base64.DEFAULT)));
                    assert encodedBytes != null;
                    SendMessage(encodedBytes.toString());
                }
            }
        });

        readMessages();

        SeenMessages();
    }

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
    }

    private void readMessages() {
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
                    adapterChat = new AdapterChat(ChatActivity.this, chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                                                                //set adapter to recycler view
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendMessage(String Message) {
        DatabaseReference df1 = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String ,Object> hashM = new HashMap<>();
        hashM.put("sender",myUID);
        hashM.put("receiver",hisUID);
      //  hashM.put("message",encMessage);
        hashM.put("message",Message);
        hashM.put("timestamp",timestamp);
        hashM.put("isseen",false);
        df1.child("Chats").push().setValue(hashM);


        MSG.setText("");
    }
    public void init() {
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username_chat);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FB_user = FirebaseDatabase.getInstance().getReference().child("users");
        userid = firebaseAuth.getCurrentUser().getUid();
        TOOLBAR = findViewById(R.id.chat_toolbar);
        MSG = findViewById(R.id.message);
        SEND_BUTTON = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.chat_recycler);
    }
    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null)
        {
            myUID = user.getUid();
        }
    }
    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }
    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }
}