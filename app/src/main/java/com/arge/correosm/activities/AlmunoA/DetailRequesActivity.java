package com.arge.correosm.activities.AlmunoA;


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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.arge.correosm.DirectionsParser;
import com.arge.correosm.R;
import com.arge.correosm.Utilidades;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DetailRequesActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

    IGoogleApi ws;
    Polyline polyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_reques);

        fab = findViewById(R.id.fab);

        mGeofireProvider = new GeofireProvider();
        mAuthProvider = new AuthProvider();

        mGoogleApiProvider = new GoogleApiProvider(DetailRequesActivity.this);

        mapView = findViewById(R.id.map_view);

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


        ws = new Retrofit.Builder().baseUrl("https:/maps.googleapis.com/").addConverterFactory(ScalarsConverterFactory.create()).build().create(IGoogleApi.class);


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
                Toast.makeText(DetailRequesActivity.this, "Permiso concedido", Toast.LENGTH_SHORT).show();
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


    private  void drawRoute(){
        System.out.println("metodo DrawRoute");

        mGoogleApiProvider.getDirection(mOriginLatLng, mDestinatioLatLong).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    System.out.println("Estoy dentro");
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String poins = polylines.getString("points");
                    mPolylineList= decodePoly(poins);
                    mPolyOptions = new PolylineOptions();
                    mPolyOptions.color(Color.DKGRAY);
                    mPolyOptions.width(15f);
                    mPolyOptions.startCap(new SquareCap());
                    mPolyOptions.jointType(JointType.ROUND);
                    mPolyOptions.addAll(mPolylineList);
                    mGoogleMap.addPolyline(mPolyOptions);

                }catch (Exception e){
                    Log.d("erro", "Error encontrado "+ e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
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

      ObtenerRutaWS(mOriginLatLng, mDestinatioLatLong);
/*
        /////////////
        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // setUpMapIfNeeded();

        // recorriendo todas las rutas
       //System.out.println("rout Back "+ Utilidades.routesBack.size());

        for(int i=0;i<Utilidades.routes.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = Utilidades.routes.get(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }
                points.add(position);
            }

            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);
            //Definimos el grosor de las Polilíneas
            lineOptions.width(2);
            //Definimos el color de la Polilíneas
            lineOptions.color(Color.BLUE);
        }

        // Dibujamos las Polilineas en el Google Map para cada ruta
       // mGoogleMap.addPolyline(lineOptions);
*/
    }

    public void ObtenerRutaWS(LatLng origenLatLng, LatLng destinationLatLng){
        String url;
        try {
             url = "https:/maps.googleapis.com/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                    + "origin=" + origenLatLng.latitude+ "," +origenLatLng.longitude + "&"
                    + "destination=" + destinationLatLng.latitude+ "," +destinationLatLng.longitude+ "&"
                    //  + "departure_time" + (new Date().getTime()) + (60*60*1000)+  "&"
                    //  + "traffic_model=best_guess&"
                    + "key=AIzaSyA5s1KOmTEPdWPZJ1A97-22KgdL68yM-BQ";

             System.out.println("url WS " + url);

            ws.getDirections(url).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    new ParseTask().execute(response.body().toString());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }catch (Exception e){

        }
    }

    public  class ParseTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>>{

        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String,String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser parser = new DirectionsParser();
                routes = parser.parse(jsonObject);
                System.out.println("se agrego new positions");
            }catch (Exception e){
                Log.e("error doIndsda", e.toString());
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> list){
            super.onPostExecute(list);
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for (int i=0; i<list.size(); i++){
                points= new ArrayList();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>>path = list.get(i);
                for (int j=0; j<path.size(); j++){
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng  position = new LatLng(lat, lng);

                    points.add(position);
                }
                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.DKGRAY);
                polylineOptions.geodesic(true);

            }
            polyline= mGoogleMap.addPolyline(polylineOptions);
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