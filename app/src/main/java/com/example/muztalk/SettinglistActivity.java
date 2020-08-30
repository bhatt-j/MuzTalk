package com.example.muztalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class SettinglistActivity extends AppCompatActivity {

    ListView listView;

    String[] s1={"Update Username","Notifications","Theme","invite friend","Change Password","Log-out","Delete Account"};
    int[] icons={R.drawable.user_icon1,R.drawable.notifications_icon,R.drawable.theme_icon,R.drawable.usergroup_icon,R.drawable.password,R.drawable.exit_icon,R.drawable.delete_icon};

    Button UPDATE_USERNAME = (Button) findViewById(R.id.a_update_pass);
    Button NOTIFICATION = (Button) findViewById(R.id.a_notifications);
    Button THEME = (Button) findViewById(R.id.a_theme);
    Button INVITE_FRIEND = (Button) findViewById(R.id.a_invite_friend);
    Button UPDATE_PASS = (Button) findViewById(R.id.a_update_pass);
    Button CHANGE_PASS = (Button) findViewById(R.id.a_change_password);
    Button LOG_OUT = (Button) findViewById(R.id.a_log_out);
    Button DELETE_ACC = (Button) findViewById(R.id.a_delete_acc);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settinglist);
        getSupportActionBar().hide();

        UPDATE_USERNAME.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { fun_updateUsername();
            }


        });







       /* ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.my_row,s1,icons);
        ListView listView = findViewById(R.id.a_settingList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position ==  0) {
                    Toast.makeText(SettinglistActivity.this, "Facebook Description", Toast.LENGTH_SHORT).show();
                }
                if (position ==  0) {
                    Toast.makeText(SettinglistActivity.this, "Whatsapp Description", Toast.LENGTH_SHORT).show();
                }
                if (position ==  0) {
                    Toast.makeText(SettinglistActivity.this, "Twitter Description", Toast.LENGTH_SHORT).show();
                }
                if (position ==  0) {
                    Toast.makeText(SettinglistActivity.this, "Instagram Description", Toast.LENGTH_SHORT).show();
                }
                if (position ==  0) {
                    Toast.makeText(SettinglistActivity.this, "Youtube Description", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

    }

    public void fun_updateUsername(){
        Intent intent = new Intent(this,UpdateusernameActivity.class);
        startActivity(intent);
    }


   /* class MyAdapter1 extends ArrayAdapter<String >
    {

        Context context;
        String[] data1 = s1;
        int[] images = icons;
        public MyAdapter1(Context ct, String[] data1, int[] images)
        {
            super(ct, my_row,R.id.options);
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
    }*/
}