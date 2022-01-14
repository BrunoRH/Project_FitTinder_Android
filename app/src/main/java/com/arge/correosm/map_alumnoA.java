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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arge.correosm.activities.AlmunoA.DetailRequesActivity;
import com.arge.correosm.providers.AuthProvider;
import com.arge.correosm.providers.GeofireProvider;
import com.arge.correosm.providers.TokenProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class map_alumnoA extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    boolean isPermisionGranted;
    MapView mapView;

    GoogleMap mGoogleMap;
    FloatingActionButton fab;

    private  FusedLocationProviderClient mLocationClient;

    private int GPS_REQUEST_CODE = 9010;

    /********/
    private LocationManager ubicacion;
    /********/
    private Marker mMarker;
    private Marker mMarkerOrigin;
    private Marker mMarkerDestin;
    private com.google.android.libraries.maps.model.LatLng mCurrentLatLong;

    private GeofireProvider mGeofireProvider;
    private List<Marker> mAlmunosBMarker = new ArrayList<>();


    private AutocompleteSupportFragment mAutoComplete;
    private PlacesClient mPlaces;

    private String mOrigin;
    private LatLng mOriginLatLng;
    private LatLng mDestinLatLng;

    /******/
    SearchView searchView;
    SearchView searchViewDestin;
    private GoogleMap.OnCameraIdleListener mCameraListener;
    /*******/

    /*******/
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
    /*********/

    private Button mButtonrequesMatch;

    private TokenProvider mTokenProvider;
    private AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_alumno_a);

        fab = findViewById(R.id.fab);

        mapView = findViewById(R.id.map_view);

        checkMyPermission();

        mGeofireProvider = new GeofireProvider();

        mLocationClient = new FusedLocationProviderClient(this);

        mTokenProvider = new TokenProvider();

        mAuthProvider = new AuthProvider();

        mButtonrequesMatch = findViewById(R.id.btnRequesMatch);

        Localizacion();
        registerUbication();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getCurrLoc();

            }
        });

        if (isPermisionGranted){
            if(isGPSenable()){
                mapView.getMapAsync(this);
                mapView.onCreate(savedInstanceState);
            }
        }

/*
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_map_key));
        }
        mPlaces = Places.createClient(this);
        mAutoComplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placesAutoCompletOrigin);
        mAutoComplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mOrigin = place.getName();
                mOriginLatLng = place.getLatLng();
                Log.d("PLACE", "Name" + mOrigin);
                Log.d("PLACE", "lat" + mOriginLatLng.latitude);
                Log.d("PLACE", "lon" + mOriginLatLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
*/

        //Search point Origin
        searchView = findViewById(R.id.sv_location);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(map_alumnoA.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mOriginLatLng = latLng;
                    if (mMarkerOrigin != null){
                        mMarkerOrigin.remove();
                    }
                    mMarkerOrigin = mGoogleMap.addMarker(new MarkerOptions().position(
                            new LatLng(latLng.latitude,latLng.longitude)
                            )
                                    .title("Origen")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location))
                    );

                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(latLng.latitude,latLng.longitude))
                                    .zoom(15f)
                                    .build()
                    ));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        //Search point Destination
        searchViewDestin = findViewById(R.id.sv_location_destin);
        searchViewDestin.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = searchViewDestin.getQuery().toString();
                List<Address> addressList = null;

                if(location != null || !location.equals("")){
                    Geocoder geocoder = new Geocoder(map_alumnoA.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mDestinLatLng = latLng;
                    if (mMarkerDestin != null){
                        mMarkerDestin.remove();
                    }
                    mMarkerDestin = mGoogleMap.addMarker(new MarkerOptions().position(
                            new LatLng(latLng.latitude,latLng.longitude)
                            )
                                    .title("Destino")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location))
                    );

                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(latLng.latitude,latLng.longitude))
                                    .zoom(15f)
                                    .build()
                    ));
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        mapView.getMapAsync(this);

        mCameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    Geocoder geocoder = new Geocoder(map_alumnoA.this);
                    mOriginLatLng = mGoogleMap.getCameraPosition().target;
                    List<Address> addressList = geocoder.getFromLocation(mOriginLatLng.latitude, mOriginLatLng.longitude, 1);
                    String city = addressList.get(0).getLocality();
                    String country = addressList.get(0).getCountryName();
                    String addres = addressList.get(0).getAddressLine(0);

                }catch (Exception e){
                    Log.d("Error", "Mensaje Error"+e.getMessage());
                }
            }
        };

        mButtonrequesMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // webServiceObtenerRuta(mOriginLatLng, mDestinLatLng);
                requesMatch();

            }
        });
        /*****/
        request= Volley.newRequestQueue(getApplicationContext());
        /*****/

        generateToken();
    }

    private void requesMatch() {
        if(mOriginLatLng != null && mDestinLatLng!= null){
            Intent intent = new Intent(map_alumnoA.this, MapsActivity.class);
            intent.putExtra("origin_lat", mOriginLatLng.latitude);
            intent.putExtra("origin_lng", mOriginLatLng.longitude);
            intent.putExtra("destination_lat", mDestinLatLng.latitude);
            intent.putExtra("destination_lng", mDestinLatLng.longitude);

           // webServiceObtenerRuta(mOriginLatLng, mDestinLatLng);

            startActivity(intent);
        }else   {
            Toast.makeText(map_alumnoA.this, "Debe seleccionar lugar de origen y destino", Toast.LENGTH_SHORT).show();
        }
    }

    /*****************/

    private void webServiceObtenerRuta(LatLng mOriginLatLng, LatLng mDestinLatLng) {

        String baseUrl = "https:/maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + mOriginLatLng.latitude+ "," +mOriginLatLng.longitude + "&"
                + "destination=" + mDestinLatLng.latitude+ "," +mDestinLatLng.longitude+ "&"
                //  + "departure_time" + (new Date().getTime()) + (60*60*1000)+  "&"
                //  + "traffic_model=best_guess&"
                + "key="+ map_alumnoA.this.getResources().getString(R.string.google_map_key);
        String url = baseUrl+query;
        System.out.println("url2 " + url);

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;
                System.out.println("Estoy en onresponse " );
                try {

                    jRoutes = response.getJSONArray("routes");

                    /** Traversing all routes */
                    for (int i = 0; i < jRoutes.length(); i++) {
                        jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
                        System.out.println("Estoy en routes " );
                        /** Traversing all legs */
                        for (int j = 0; j < jLegs.length(); j++) {
                            jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                            System.out.println(" estoy en legs " );
                            /** Traversing all steps */
                            for (int k = 0; k < jSteps.length(); k++) {
                                String polyline = "";
                                polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);
                                System.out.println(" estoy en steps " );
                                /** Traversing all points */
                                for (int l = 0; l < list.size(); l++) {
                                    System.out.println("estoy en point " );
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                                    hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                                    path.add(hm);

                                }
                            }
                            Utilidades.routes.add(path);

                            List listRoutes = Utilidades.routes;

                           // Utilidades.printLis();



                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        }
        );

        request.add(jsonObjectRequest);
    }
/*
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){
        //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
        //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
        //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
   /*         for(int i=0;i<jRoutes.length();i++){
                System.out.println("estoy en routes 2 " );
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
    /*            for(int j=0;j<jLegs.length();j++){
                    System.out.println("estoy en legs 2 " );
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
    /*                for(int k=0;k<jSteps.length();k++){
                        System.out.println("estoy en steps 2 " );
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
      /*                  for(int l=0;l<list.size();l++){
                            System.out.println("estoy en point 2 " );
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    Utilidades.routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return Utilidades.routes;
    }
*/

    private List<LatLng> decodePoly(String encoded) {
        System.out.println("estoy en decodePoly");
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    /***************/


    private boolean isGPSenable(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Boolean providerEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnable){
            return  true;
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(this  )
                    .setTitle("holi")
                    .setMessage("sda")
                    .setPositiveButton("Si", ((dialogInterface, i) -> {
                        Intent intent =  new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS );
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
                //gotoLocation(location.getLatitude(), location.getLongitude());
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoLocation(location.getLatitude(), location.getLongitude());

                    }
                });

                getActiveAlumnosB(location.getLatitude(), location.getLongitude());
            }
        });
    }

    public void gotoLocation(double latitude, double longitude) {
        LatLng LatLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng, 12);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (mMarker != null){
            mMarker.remove();
        }
/*
        mMarker = mGoogleMap.addMarker(new MarkerOptions().position(
                new LatLng(latitude,longitude)
                )
                        .title("Tu posicion")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons8_user_location_96px))
        );
*/
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(latitude,longitude))
                        .zoom(12f)
                        .build()
        ));

    }

    private void Localizacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1000);
        }
        ubicacion = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location log = ubicacion.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (ubicacion != null) {
            getCurrLoc();
        }
    }


    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(map_alumnoA.this, "Permiso concedido", Toast.LENGTH_SHORT).show();
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

    private void getActiveAlumnosB(double latitude, double longitude){
        mGeofireProvider.getActiveAlumnoB(latitude, longitude,15 ).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //añadiremos los marcadores de los alumnos B
                for (Marker marker: mAlmunosBMarker){
                    if(marker.getTag()!= null){
                        if(marker.getTag().equals(key)){
                            return;
                        }
                    }
                }
                LatLng alumnoBLatlong = new LatLng(location.latitude, location.longitude);
                Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(alumnoBLatlong).title("Corredor disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_mapa)));
                marker.setTag(key);
                mAlmunosBMarker.add(marker);
            }

            @Override
            public void onKeyExited(String key) {
                //eliminamos el marcador de los alumnos B
                for (Marker marker: mAlmunosBMarker){
                    if(marker.getTag()!= null){
                        if(marker.getTag().equals(key)){
                            marker.remove();
                            mAlmunosBMarker.remove(marker);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            //actualizamos la posicion de cada alumno B
                for (Marker marker: mAlmunosBMarker){
                    if(marker.getTag()!= null){
                        if(marker.getTag().equals(key)){
                            marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
       // mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

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

        @Override
        public void onLocationChanged(@NonNull Location location) {
            getCurrLoc();
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






















