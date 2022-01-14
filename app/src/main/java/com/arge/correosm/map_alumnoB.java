package com.arge.correosm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arge.correosm.providers.AuthProvider;
import com.arge.correosm.providers.GeofireProvider;
import com.arge.correosm.providers.TokenProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.security.PrivilegedAction;
import java.util.List;

public class map_alumnoB extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    private TokenProvider mTokenProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_alumno_b);

        fab = findViewById(R.id.fab);

        mGeofireProvider = new GeofireProvider();
        mAuthProvider = new AuthProvider();

        mTokenProvider = new TokenProvider();

        mapView = findViewById(R.id.map_view);

        checkMyPermission();

        mLocationClient = new FusedLocationProviderClient(this);

        Localizacion();
        registerUbication();

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
        generateToken();
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

    private void registerUbication() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new miLocationListener());
    }

    @SuppressLint("MissingPermission")
    public void getCurrLoc() {

            mLocationClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Location location = task.getResult();
                    /***
                     mCurrentLantlong = new LatLng(location.getLatitude(), location.getLongitude());
                     updateLocation();
                     /***/
                    /***/
                    updateLocation(location.getLatitude(), location.getLongitude());
                    /***/

                    gotoLocation(location.getLatitude(), location.getLongitude());
                }
            });


    }
    /*
    private void updateLocation(){
       // mGeofireProvider.saveLocation(mAuthProvider.GetID(), double latitude, double longitude);
    }*/

    private void updateLocation(double latitude, double longitude){
        mGeofireProvider.saveLocation(mAuthProvider.GetID(), latitude, longitude);

    }

    public void gotoLocation(double latitude, double longitude) {
        LatLng LatLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng, 15f);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (mMarker != null){
            mMarker.remove();
        }

        mMarker = mGoogleMap.addMarker(new MarkerOptions().position(
                new LatLng(latitude,longitude)
                )
                .title("Tu posicion")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mapa))
        );
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                .target(new LatLng(latitude,longitude))
                .zoom(15f)
                .build()
        ));

    }

    private void disconect(){
        mButtonConect.setText("CONECTARSE");
        mGeofireProvider.removeLocation(mAuthProvider.GetID());
        isConect = false;
    }


    private void Localizacion() {

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

    }


    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(map_alumnoB.this, "Permiso concedido", Toast.LENGTH_SHORT).show();
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


        mButtonConect =  findViewById(R.id.btnConect);
        mButtonConect.setText("DESCONECTARSE");
        mButtonConect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConect){
                    disconect();
                }else{
                    isConect = true;
                    mButtonConect.setText("DESCONECTARSE");
                    Localizacion();
                }
            }
        });

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


    /******/
    private class miLocationListener implements LocationListener {
        /**********Este metodo actualiza mi posicion cada segundo********/
        @Override
        public void onLocationChanged(@NonNull Location location) {

            //System.out.println("Se cambio la ubicaicon");
            //Toast.makeText(map_alumnoB.this, "nueva pisiccion", Toast.LENGTH_SHORT).show();

            if(isConect){
                getCurrLoc();
            }

        }

        @Override
        public void onLocationChanged(@NonNull List<Location> locations) {

        }

        @Override
        public void onFlushComplete(int requestCode) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }

    public void generateToken(){
        mTokenProvider.create(mAuthProvider.GetID());
    }
}