package com.example.muztalk;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class get_device_token_service extends FirebaseInstanceIdService {

    public void onTokenRefresh()
    {
        String DeviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("DEVICE TOKEN:",DeviceToken);

    }

}
