package com.example.muztalk;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreff {
    SharedPreferences sharedPreferences;
     public SharedPreff (Context context)
     {
         sharedPreferences = context.getSharedPreferences("filename",Context.MODE_PRIVATE);
     }

     public void setNightModeState(Boolean state)
     {
         SharedPreferences.Editor editor = sharedPreferences.edit();
         editor.putBoolean("NightMode",state);
         editor.apply();
         editor.commit();
     }

     public Boolean loadNightModeState()
     {
         Boolean state = sharedPreferences.getBoolean("NightMode",false);
         return state;
     }
}
