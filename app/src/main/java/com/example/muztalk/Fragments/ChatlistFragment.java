package com.example.muztalk.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muztalk.Adapters.AdapterChatlist;
import com.example.muztalk.Models.ModelChat;
import com.example.muztalk.Models.ModelChatList;
import com.example.muztalk.Models.ModelUsers;
import com.example.muztalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatlistFragment extends Fragment {
    ProgressBar PROGRESSBAR;
    FirebaseAuth firebaseAuth;
    RecyclerView RECYCLER_CHAT_LIST;
    List<ModelChatList> chatListList;
    List<ModelUsers> userList;
    DatabaseReference databaseReference;
    FirebaseUser currentuser;
    AdapterChatlist adapterChatlist;

    public ChatlistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatlist, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        RECYCLER_CHAT_LIST=view.findViewById(R.id.recyclerView_chatList);
        PROGRESSBAR=view.findViewById(R.id.progressBar);
        PROGRESSBAR.setVisibility(View.GONE);
        chatListList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(currentuser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatListList.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    ModelChatList chatList = ds.getValue(ModelChatList.class);
                    chatListList.add(chatList);
                }
                loadChats();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void loadChats() {
        PROGRESSBAR.setVisibility(View.VISIBLE);
        userList = new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               userList.clear();
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    ModelUsers users = ds.getValue(ModelUsers.class);
                    for(ModelChatList chatList: chatListList)
                    {
                        if(users.getId() != null && users.getId().equals(chatList.getId()))
                        {
                            userList.add(users);
                            break;
                        }
                        PROGRESSBAR.setVisibility(View.GONE);
                        ///adapter
                        adapterChatlist = new AdapterChatlist(getContext(), userList);
                        //set Adapter
                        RECYCLER_CHAT_LIST.setAdapter(adapterChatlist);
                        //set last message
                        for(int i=0; i<userList.size(); i++)
                        {
                            lastMessage(userList.get(i).getId());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lastMessage = "default";
                for(DataSnapshot ds: snapshot.getChildren())
                {
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat==null)
                    {
                        continue;
                    }
                    String sender =chat.getSender();
                    String receiver = chat.getReceiver();
                    if(sender == null || receiver == null)
                    {
                        continue;
                    }
                    if(chat.getReceiver().equals(currentuser.getUid()) && chat.getSender().equals(id) || chat.getReceiver().equals(id)
                                                                                            && chat.getSender().equals(currentuser.getUid()))
                    {
                            lastMessage = chat.getMessage();
                    }
                }
                //set adapter
                adapterChatlist.setLastMessageMap(id, lastMessage);
                adapterChatlist.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}