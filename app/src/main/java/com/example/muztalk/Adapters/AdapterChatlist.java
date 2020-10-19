package com.example.muztalk.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.muztalk.ChatActivity;
import com.example.muztalk.Models.ModelUsers;
import com.example.muztalk.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatlist extends RecyclerView.Adapter<AdapterChatlist.MyHolder>{

    Context context;
    List<ModelUsers> userList;
    private HashMap<String, String> lastMessageMap;

    //constructor
    public AdapterChatlist(Context context, List<ModelUsers> userList)
    {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String hisUid = userList.get(position).getId();
        //////////////////////////////String userImage = userList.get(position).getImageURL();
        String username = userList.get(position).getUsername();
        String last_message = lastMessageMap.get(hisUid);

        //set data
        holder.USER_NAME.setText(username);
        if (last_message == null || last_message.equals("default")) {
            holder.LAST_MESSAGE.setVisibility(View.GONE);
        } else {
            holder.LAST_MESSAGE.setVisibility(View.VISIBLE);
            holder.LAST_MESSAGE.setText(last_message);
        }
        try{
            Picasso.get().load(R.drawable.logo).placeholder(R.drawable.logo).into(holder.PROFILE_DP);
        }
        catch (Exception e)
        {
            Picasso.get().load(R.drawable.logo).into(holder.PROFILE_DP);
        }

        //online status
        if(userList.get(position).getStatus().equals("Active"))
        {
            //////////////////////////////////////////////////////ONLINE
            //Picasso.get().load(R.drawable.circle_online).into(holder.ONLINE_STATUS);
            holder.ONLINE_STATUS.setImageResource(R.drawable.circle_online);
        }
        else
        {
            //////////////////////////////////////////////////////OFFLINE
            holder.ONLINE_STATUS.setImageResource(R.drawable.circle_offline);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start chat activity with user
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUID",hisUid);
                context.startActivity(intent);
            }
        });
    }

    public void setLastMessageMap(String userId, String lastMessage)
    {
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{
        //views of row chat list
        ImageView PROFILE_DP, ONLINE_STATUS;
        TextView USER_NAME, LAST_MESSAGE;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            PROFILE_DP = itemView.findViewById(R.id.user_chatList_dp);
            ONLINE_STATUS = itemView.findViewById(R.id.online_status);
            USER_NAME = itemView.findViewById(R.id.username_chatList);
            LAST_MESSAGE = itemView.findViewById(R.id.last_msg);


        }
    }
}
