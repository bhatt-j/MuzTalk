package com.example.muztalk.adapter;

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
import com.example.muztalk.R;
import com.example.muztalk.model.ModelUsers;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.muztalk.R.layout.row_users;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder>{
    Context context;
    List<ModelUsers> usersList;

    public AdapterUsers(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(row_users,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //getdata
        String hisUID = usersList.get(position).getId();
        String userImage = usersList.get(position).getImageURL();
        String userName = usersList.get(position).getUsername();

        //setdata
        MyHolder.name.setText(userName);
        try
        {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.logo)
                    .into(MyHolder.dp) ;
        }
        catch (Exception e)
        {

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUID",hisUID);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();

    }

    static class MyHolder extends RecyclerView.ViewHolder{
        static ImageView dp;
        static TextView name;
        static TextView typing;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.user_list_dp);
            name = itemView.findViewById(R.id.username_list);
            typing = itemView.findViewById(R.id.typing);
        }
    }
}
