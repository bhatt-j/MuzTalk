package com.example.muztalk;

//public class MyAdapter extends ArrayAdapter<MyAdapter.MyViewHolder> {
    /*Context context;
    String[] data1 ;
    int[] images;
    public MyAdapter(Context ct, String[] s1, int[] icons)
    {
        super(ct, R.layout.my_row,R.id.options);
        this.context = ct;
        this.data1 = s1;
        this.images = icons;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("ViewHolder") View myrow = layoutInflater.inflate(my_row, parent, false);
        ImageView images = myrow.findViewById(R.id.icons);
        TextView options = myrow.findViewById(R.id.options);
        images.setImageResource(icons[position]);
        options.setText(data1[position]);
        return super.getView(position, convertView, parent);
    }

    public class MyViewHolder {
    }
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
                   Intent intent = new Intent(context, UpdateusernameActivity.class);
                   context.startActivity(intent);

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
    }*/
//}
