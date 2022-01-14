package com.arge.correosm;

import static android.app.PendingIntent.getActivity;
import static com.arge.correosm.util.DecodePoints.decodePoly;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arge.correosm.DirectionsParser;
import com.arge.correosm.R;
import com.arge.correosm.Utilidades;
import com.arge.correosm.activities.AlmunoA.RecuesAlumnoBActivity;
import com.arge.correosm.activities.AlumnoB.RegisterBActivity;
import com.arge.correosm.providers.AuthProvider;
import com.arge.correosm.providers.GeofireProvider;
import com.arge.correosm.providers.GoogleApiProvider;
import com.arge.correosm.retrofit.IGoogleApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.PolyUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    boolean isPermisionGranted;
    MapView mapView;

    GoogleMap mGoogleMap;
    FloatingActionButton fab;

    private FusedLocationProviderClient mLocationClient;

    private int GPS_REQUEST_CODE = 9010;

    /********/
    private LocationManager ubicacion;
    private AuthProvider mAuthProvider;
    /********/

    private Marker mMarker;

    private Button mButtonConect;
    private boolean isConect = true;

    private LatLng mCurrentLantlong;
    private GeofireProvider mGeofireProvider;

    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private double mExtraDestinantionLat;
    private double mExtraDestinantionLng;

    private LatLng mOriginLatLng;
    private LatLng mDestinatioLatLong;

    private GoogleApiProvider mGoogleApiProvider;

    private List<LatLng> mPolylineList;
    private PolylineOptions  mPolyOptions;

    JSONObject jso;

    private TextView mTextViewOrigin;
    private TextView mTextViewDestination;
    private TextView mTextViewTime;
    private TextView mTextViewDistance;
    private TextView mTextViewendAddress;
    private TextView mTextViewStartAddreess;

    private Button mbtnReguesNow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fab = findViewById(R.id.fab);

        mGeofireProvider = new GeofireProvider();
        mAuthProvider = new AuthProvider();

        mGoogleApiProvider = new GoogleApiProvider(MapsActivity.this);

        mapView = findViewById(R.id.map_view);

        mTextViewOrigin = findViewById(R.id.textViewOrigin);
        mTextViewDestination = findViewById(R.id.textViewDestin);
        mTextViewTime = findViewById(R.id.textViewTime);
        mTextViewDistance = findViewById(R.id.textViewDistance);
        mTextViewendAddress = findViewById(R.id.textViewDestin);
        mTextViewStartAddreess = findViewById(R.id.textViewOrigin);

        mbtnReguesNow = findViewById(R.id.btnReguesNow);

        mbtnReguesNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRequesAlumnoB();
            }
        });

        checkMyPermission();

        mLocationClient = new FusedLocationProviderClient(this);

        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng", 0);
        mExtraDestinantionLat = getIntent().getDoubleExtra("destination_lat", 0);
        mExtraDestinantionLng = getIntent().getDoubleExtra("destination_lng", 0);

        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinatioLatLong = new LatLng(mExtraDestinantionLat, mExtraDestinantionLng);
        // Localizacion();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getCurrLoc();
                Localizacion();
            }
        });

        if (isPermisionGranted) {
            if (isGPSenable()) {
                mapView.getMapAsync(this);
                mapView.onCreate(savedInstanceState);

            }
        }

    }

    private void goToRequesAlumnoB(){
        Intent intent = new Intent(MapsActivity.this, RecuesAlumnoBActivity.class);
        intent.putExtra("origin_lat" , mOriginLatLng.latitude);
        intent.putExtra("origin_lng" , mOriginLatLng.longitude);


        startActivity(intent);
        finish();
    }

    private boolean isGPSenable() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnable) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("holi")
                    .setMessage("sda")
                    .setPositiveButton("Si", ((dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);
                    }))
                    .setCancelable(false)
                    .show();
        }
        return false;
    }

    /*
    private void updateLocation(){
       // mGeofireProvider.saveLocation(mAuthProvider.GetID(), double latitude, double longitude);
    }*/


    private void Localizacion() {
/*
        Toast.makeText(map_alumnoB.this, "localizacion", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1000);
        }
        ubicacion = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //OBTENEMOS la ultima posicion de la ubicacion actual
        Location log = ubicacion.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (ubicacion != null) {
            Toast.makeText(map_alumnoB.this, "ir a get Currloc", Toast.LENGTH_SHORT).show();
            getCurrLoc();
        }
*/
    }


    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(MapsActivity.this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                isPermisionGranted = true;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

        mGoogleMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_star)));
        mGoogleMap.addMarker(new MarkerOptions().position(mDestinatioLatLong).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_end)));

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(mOriginLatLng)
                        .zoom(14f)
                        .build()
        ));

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        String baseUrl = "https:/maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + mOriginLatLng.latitude+ "," +mOriginLatLng.longitude + "&"
                + "destination=" + mDestinatioLatLong.latitude+ "," +mDestinatioLatLong.longitude+ "&"
                //  + "departure_time" + (new Date().getTime()) + (60*60*1000)+  "&"
                //  + "traffic_model=best_guess&"
                + "key=AIzaSyA5s1KOmTEPdWPZJ1A97-22KgdL68yM-BQ";
        String url = baseUrl+query;

        System.out.println("url MapsAc: " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    jso = new JSONObject(response);
                    trazarRuta(jso);
                    //Log.i("jsonRutaA: ", ""+response);

                    getDistanceAndDuration(jso);



                } catch (JSONException e) {
                    System.out.println("errorJSONruta "+e);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    public void getDistanceAndDuration(JSONObject jso){
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray distanceJson;
        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){

                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");

                for (int j=0; j<jLegs.length();j++){

                    String distance = ""+((JSONObject)((JSONObject)jLegs.get(j)).get("distance")).get("text");
                    String duration = ""+((JSONObject)((JSONObject)jLegs.get(j)).get("duration")).get("text");
                    String start_address = ""+((JSONObject) jLegs.get(j)).get("start_address");
                    String end_address = ""+((JSONObject) jLegs.get(j)).get("end_address");
                    mTextViewDistance.setText(distance);
                    mTextViewTime.setText(duration);

                    mTextViewStartAddreess.setText(start_address);
                    mTextViewendAddress.setText(end_address);

                    Log.i("distance",""+distance);

                    /*JSONArray legs = jso.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distancec = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distancec.getString("text");
                    String durationText = duration.getString("text");
                    mTextViewDistance.setText(durationText);
                    mTextViewDistance.setText(distanceText);*/

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void trazarRuta(JSONObject jso){
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){

                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");

                for (int j=0; j<jLegs.length();j++){

                    jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");

                    for (int k = 0; k<jSteps.length();k++){

                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        Log.i("end",""+polyline);
                        List<LatLng> list = PolyUtil.decode(polyline);
                        mGoogleMap.addPolyline(new PolylineOptions().addAll(list).color(Color.GRAY).width(10).jointType(JointType.ROUND));

                    }
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        //Toast.makeText(map_alumnoB.this, "movido", Toast.LENGTH_SHORT).show();
        //Localizacion();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GPS_REQUEST_CODE){
            LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);

            System.out.println("Estoy en location Manager");

            Boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(providerEnable){
                Toast.makeText(this, "GPS activo", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "GPS no activo", Toast.LENGTH_SHORT).show();
            }
        }
    }


}