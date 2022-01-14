package com.arge.correosm.retrofit;

import com.arge.correosm.models.FCMBody;
import com.arge.correosm.models.FCMRResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA-Pzzqnc:APA91bFRmE_u-hVtNpeCi1_oiDGvjRoYZunn2PL65Vm5ZBGPPh3GWNwevLGDikXtL3g4z3LFF2cNxAVSClRhUX8AhURooL1eQxD1sSCnkfbo_IWd92LCcLDwmVl6iVSRl9zF8RniKA03"
    })
    @POST("fcm/send")
    Call<FCMRResponse> send(@Body FCMBody body);
}














