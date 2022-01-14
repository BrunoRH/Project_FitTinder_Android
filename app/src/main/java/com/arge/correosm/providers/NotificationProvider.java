package com.arge.correosm.providers;

import com.arge.correosm.models.FCMBody;
import com.arge.correosm.models.FCMRResponse;
import com.arge.correosm.retrofit.IFCMApi;
import com.arge.correosm.retrofit.RetrofilAlumnoA;

import retrofit2.Call;

public class NotificationProvider {

    private String url= "https://fcm.googleapis.com";

    public NotificationProvider(){

    }

    public Call <FCMRResponse> sendNotification(FCMBody body){
        return RetrofilAlumnoA.getAlumnoAobject(url).create(IFCMApi.class).send(body);
    }
}












