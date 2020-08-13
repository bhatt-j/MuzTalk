package com.example.muztalk;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {
    private ListView listView;
     String[] SongNames ;


    ListView mylistView;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mylistView = (ListView) findViewById(R.id.a_mysonglist);
        runtime_permission();

        final ImageView a_spotify = (ImageView) findViewById(R.id.a_spotify);
        a_spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getPackageManager().getLaunchIntentForPackage("com.android.vending");
                startActivity(i);
            }
        });

    }


    public void runtime_permission()
    {
    Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(new PermissionListener() {
                @Override
                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                    Toast.makeText(MusicActivity.this,"your Songs",Toast.LENGTH_SHORT).show();
                    displaysongs();
                }
                @Override
                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                    Toast.makeText(MusicActivity.this,"Cannot fetch your data",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                    permissionToken.continuePermissionRequest();
                }
            }).check();
}

    public ArrayList<File> findSong(File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for(File singleFile: files)
        {
            if(singleFile.isDirectory() && singleFile.isHidden())
            {
                arrayList.addAll( findSong(singleFile));
            }
            else{
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav"))
                {
                    arrayList.add(singleFile);
                }
            }

        }

        return arrayList;
    }

    void displaysongs()
    {
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        SongNames = new  String[mySongs.size()];

        for(int i=0; i<mySongs.size();i++)
        {
            SongNames[i]=mySongs.get(i).getName().toString().replace("mp3","").replace(".wav","");
        }

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,SongNames);
        mylistView.setAdapter(myAdapter);

    }


}




   /*  listView = findViewById(R.id.listView);

        ArrayList<File> songs = readSongs(Environment.getExternalStorageDirectory());

        SongNames = new String[songs.size()];

        for(int i = 0; i< songs.size(); ++i)
        {
            SongNames[i]= songs.get(i).getName().toString().replace(".mp3","");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.mysongs_layout,R.id.textView,SongNames);
        listView.setAdapter(adapter);
         listView.setOnItemClickListener(new OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView parent, View view, int position, long id) {
                 int i;
                startActivity(new Intent(MusicActivity.this,SongplayerActivity.class).putExtra("position",i).putExtra("list",SongNames));
             }
         });

*/

  /*  private ArrayList<File> readSongs(File root){
        ArrayList<File> arrayList = new ArrayList<File>();
        File files[] = root.listFiles();

        for(File file : files){
            if(file.isDirectory())
            {
                arrayList.addAll(readSongs(file));
            }
            else
            {
                if(file.getName().endsWith(".mp3"))
                {
                    arrayList.add(file);
                }
            }
        }
        return arrayList;
    }
}*/