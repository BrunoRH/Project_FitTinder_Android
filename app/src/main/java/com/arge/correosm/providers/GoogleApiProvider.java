package com.arge.correosm.providers;

import android.content.Context;
import android.widget.Toast;

import com.arge.correosm.R;
import com.arge.correosm.retrofit.IGoogleApi;
import com.arge.correosm.retrofit.RetrofilAlumnoA;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import retrofit2.Call;

public class GoogleApiProvider {

    private Context context;

    public GoogleApiProvider(Context context) {
        this.context = context;
    }

    public Call<String> getDirection(LatLng origenLatLng, LatLng destinationLatLng){
        String baseUrl = "https:/maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + origenLatLng.latitude+ "," +origenLatLng.longitude + "&"
                + "destination=" + destinationLatLng.latitude+ "," +destinationLatLng.longitude+ "&"
              //  + "departure_time" + (new Date().getTime()) + (60*60*1000)+  "&"
              //  + "traffic_model=best_guess&"
                + "key="+ context.getResources().getString(R.string.google_map_key);

        return RetrofilAlumnoA.getAlumnoA(baseUrl).create(IGoogleApi.class).getDirections(baseUrl+query);
    }
}










