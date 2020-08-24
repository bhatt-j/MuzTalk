package com.example.muztalk;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    String[] data1;
    Context context;
    int[] images;
    public MyAdapter(Context ct, String[] s1,int[] icons)
    {
        context = ct;
        data1 = s1;
        images = icons;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row,parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
           holder.text1.setText(data1[position]);
           holder.icn.setImageResource(images[position]);

           holder.my_layout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(context, PersonaldetailsActivity.class);
                   context.startActivity(intent);

                   /*final Intent intent;
                   switch (images.length){
                       case 0:
                           intent =  new Intent(context, PersonaldetailsActivity.class);
                           break;

                       case 1:
                           intent =  new Intent(context, SecondActivity.class);
                           break;

                       default:
                           intent =  new Intent(context, DefaultActivity.class);
                           break;
                   }
                   context.startActivity(intent);*/
               }
           });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text1;
        ImageView icn;
        ConstraintLayout my_layout;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            text1 = itemView.findViewById(R.id.options);
            icn=itemView.findViewById(R.id.icons);
            my_layout=itemView.findViewById(R.id.my_layout);
        }
    }
}
