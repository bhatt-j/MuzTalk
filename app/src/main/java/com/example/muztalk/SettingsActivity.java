package com.example.muztalk;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SettingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    String[] s1;
    int[] icons={R.drawable.user_icon1,R.drawable.notifications_icon,R.drawable.theme_icon,R.drawable.usergroup_icon,R.drawable.password,R.drawable.exit_icon,R.drawable.delete_icon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        recyclerView = findViewById(R.id.recycler_view);
        s1=getResources().getStringArray(R.array.settings_menu);

        MyAdapter MyAdapter = new MyAdapter(this,s1,icons);
        recyclerView.setAdapter(MyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }
}