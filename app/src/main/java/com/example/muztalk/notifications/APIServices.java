package com.example.muztalk.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIServices {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA0vAzif8:APA91bE9g8Vptt2Q3gV4cgqktkpvCfIagj4wNtYnOyWBy1xg7m4X-JJI3NLjM7zrw1HDutOGtKqr9V58YGYHEaxnCh_XqryEWWlP_dHFmk_R_fCV7NxkpOTUOEfCkV_J4Stx80e3yEcf"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}
