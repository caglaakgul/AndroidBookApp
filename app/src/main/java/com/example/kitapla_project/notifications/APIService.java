package com.example.kitapla_project.notifications;

import com.example.kitapla_project.models.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application-json",
            "Authorization:key=AAAAtko3HN8:APA91bFWKwwqTQEg-maSingjiBYG1fuqb5aGYKwtJTgt3sgLZTKzPoAQt1CIHRm-8aXOW6V57dxYp1Yr-nYgPKYkorf2GpmiRYYsjYIwxaQ6WsoyS8BBC17J0MOfktYZqvEarTTSVjdr"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}
