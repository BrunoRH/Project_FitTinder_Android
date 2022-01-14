package com.arge.correosm.activities.AlmunoA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.arge.correosm.R;
import com.arge.correosm.models.FCMBody;
import com.arge.correosm.models.FCMRResponse;
import com.arge.correosm.providers.GeofireProvider;
import com.arge.correosm.providers.NotificationProvider;
import com.arge.correosm.providers.TokenProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuesAlumnoBActivity extends AppCompatActivity {

    private LottieAnimationView mAnimation;
    private TextView mTextViewLookinFor;
    private Button mButtonCancelRequest;
    private GeofireProvider mGeoFireProvider;
    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private LatLng mOriginLatLng;

    private double mRadius = 0.1;
    private boolean mDriverFound = false;
    private String mIdAlumnoBFound = "";
    private LatLng mAlumnoBFoundLatLng;

    private NotificationProvider mNotificationProvider;
    private TokenProvider mTokenProvier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recues_alumno_bactivity);

        mButtonCancelRequest = findViewById(R.id.btnCancelRequest);
        mTextViewLookinFor = findViewById(R.id.textViewLookkingFor);

        mExtraOriginLat= getIntent().getDoubleExtra("origin_lat",0);
        mExtraOriginLng= getIntent().getDoubleExtra("origin_lng",0);
        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);

        mNotificationProvider = new NotificationProvider();
        mTokenProvier = new TokenProvider();

        mGeoFireProvider = new GeofireProvider();
        getClosesAlumnosB();
    }

    private void getClosesAlumnosB(){

        mGeoFireProvider.getActiveAlumnoB(mOriginLatLng.latitude, mOriginLatLng.longitude , mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if(!mDriverFound){
                    mDriverFound= true ;
                    mIdAlumnoBFound = key;
                    mAlumnoBFoundLatLng = new LatLng(location.latitude, location.longitude);
                    mTextViewLookinFor.setText("CORREDOR ENCONTRADO\nESPERANDO RESPUESTA");

                    sendNotification();

                    Log.d("AlumoB ", "ID: "+mIdAlumnoBFound);
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //Iingrea al terminar la busqueda en un radio de 0.1 km

                if(!mDriverFound){
                    mRadius = mRadius + 0.1f;
                    if(mRadius > 15){
                        mTextViewLookinFor.setText("NO SE ECONTRÓ UN CORREDOR");
                        Toast.makeText(RecuesAlumnoBActivity.this, "NO SE ENCONTRÓ UN CORREDOR", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        getClosesAlumnosB();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    public void sendNotification(){

        mTokenProvier.getToken(mIdAlumnoBFound).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();
                Map<String, String> map = new HashMap<>();
                map.put("title", "Solicitud de servivio");
                map.put("bady", "Un alumnoA esta solicitando un servicio");
                FCMBody fcmBody = new FCMBody(token, "high", map);
                mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMRResponse>() {
                    @Override
                    public void onResponse(Call<FCMRResponse> call, Response<FCMRResponse> response) {
                        if(response.body() != null){
                            if (response.body().getSuccess() == 1){
                                Toast.makeText(RecuesAlumnoBActivity.this, "La notificaión se ha enviado correctamente", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(RecuesAlumnoBActivity.this, "No se puso enviar la notificación", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMRResponse> call, Throwable t) {
                        Log.d("Error", "Error " + t.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}














