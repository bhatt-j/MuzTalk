package com.example.muztalk.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muztalk.R;
import com.example.muztalk.model.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 0;
    FirebaseUser fUser;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;

    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right_send,parent,false);
            return new MyHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left_recv,parent,false);
            return new MyHolder(view);
        }
    }

    @SuppressLint({"SetTextI18n", "WrongConstant"})
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //getdata
        String MESSAGE = chatList.get(position).getMessage();
        String TimeStamp = chatList.get(position).getTimestamp();

        //convert time stamp to dd/mm/yyyy format
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(TimeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
        int visiblity = 1;

        //decrypt data

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

        byte[] decodedBytes = null;
        byte[] decode_msg = null;
        decode_msg = MESSAGE.getBytes();
        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, publicKey);
            decodedBytes = c.doFinal(decode_msg);
        } catch (Exception e) {
            Log.e("Crypto", "RSA decryption error");
        }
      Log.d("Decoded string: ", new String(decodedBytes));
        String decrypted_msg = new String(decodedBytes);


        byte[] DECODEDBYTES = null;

        try {
            Cipher c = Cipher.getInstance("RSA");
            c.init(Cipher.DECRYPT_MODE, publicKey);
            DECODEDBYTES = c.doFinal(decodedBytes);
        } catch (Exception e) {
            Log.e("Crypto", "RSA decryption error");
        }
        Log.d("Decoded string: ", new String(DECODEDBYTES));
        String DECRYPTED_MSG = new String(DECODEDBYTES);



        //setdata
        holder.MESSAGE.setText(decrypted_msg);
        holder.TIME.setVisibility(visiblity);
        holder.TIME.setText(dateTime);
        try{
            Picasso.get().load(imageUrl).into(holder.RECV_PROFILE_PIC);
        }
        catch (Exception e)
        {
            //
        }

        /////////////////////////////////////////////////////////////////////click to show delete dialog box
        /*holder.MESSAGELAYOUT.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });*/

        holder.MESSAGELAYOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteMessage(position);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });


        /////////////////////////////////////////////////////////////////////set seen/delivered status
        if(position==chatList.size()-1)
        {
            if(chatList.get(position).isIsseen())
            {
                holder.SEEN_STATUS.setVisibility(visiblity);
                holder.SEEN_STATUS.setText("Seen");
            }
            else{
                holder.SEEN_STATUS.setVisibility(visiblity);
                holder.SEEN_STATUS.setText("Delivered");
            }
        }
        else
        {
            holder.SEEN_STATUS.setVisibility(View.GONE);
        }


    }

    private void DeleteMessage(int position) {
        //compare timestamp of selected and stored message and delete
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String myUID = user.getUid();
        String msgTimeStamp = chatList.get(position).getTimestamp();
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dRef.orderByChild("timestamp").equalTo(msgTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    if(ds.child("sender").getValue().equals(myUID))
                    {
                        ////////////////////////////////////////////////////////////1)remove message from Chats
                        //ds.getRef().removeValue();
                        ////////////////////////////////////////////////////////////2)set the value as "This message was deleted..."
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message","This message was deleted...");
                        ds.getRef().updateChildren(hashMap);
                        Toast.makeText(context,"message deleted...",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(context,"You can only delete your message.",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }

    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        ImageView RECV_PROFILE_PIC;
        TextView MESSAGE, TIME, SEEN_STATUS;
        RelativeLayout MESSAGELAYOUT;//for click listener to show delete
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            RECV_PROFILE_PIC = itemView.findViewById(R.id.recv_profile_pic);
            MESSAGE = itemView.findViewById(R.id.message);
            TIME = itemView.findViewById(R.id.timestamp);
            SEEN_STATUS = itemView.findViewById(R.id.delivr_status);
            MESSAGELAYOUT = itemView.findViewById(R.id.messageLayout);

        }
    }
}
