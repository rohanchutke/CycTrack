package com.example.cyctrack;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;

import com.mapbox.mapboxsdk.geometry.LatLng;

import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapNavActivity extends AppCompatActivity implements OnNavigationReadyCallback,
        NavigationListener, LocationEngineListener, LocationListener {


    private static Point ORIGIN = Point.fromLngLat(121.3311219571, 31.1517889257);
    private static Point DESTINATION = Point.fromLngLat(121.3149287837, 31.1393712551);

    private NavigationView navigationView;
    private LocationEngine locationEngine;
    private Location originLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map_nav);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);

        SharedPreferences dest_result_lat = getSharedPreferences("Destination_lat_for_nav", Context.MODE_PRIVATE);
        String dest_value_lat = dest_result_lat.getString("Destination_lat_for_nav_value", "Data not found");
        double dest_value_lat_double = Double.parseDouble(dest_value_lat);

        SharedPreferences dest_result_long = getSharedPreferences("Destination_long_for_nav", Context.MODE_PRIVATE);
        String dest_value_long = dest_result_long.getString("Destination_long_for_nav_value", "Data not found");
        double dest_value_long_double = Double.parseDouble(dest_value_long);

        SharedPreferences source_result_lat = getSharedPreferences("Source_lat_for_nav", Context.MODE_PRIVATE);
        String source_value_lat = source_result_lat.getString("Source_lat_for_nav_value", "Data not found");
        double source_value_lat_double = Double.parseDouble(source_value_lat);

        SharedPreferences source_result_long = getSharedPreferences("Source_long_for_nav", Context.MODE_PRIVATE);
        String source_value_long = source_result_long.getString("Source_long_for_nav_value", "Data not found");
        double source_value_long_double = Double.parseDouble(source_value_long);


        DESTINATION = Point.fromLngLat(dest_value_long_double, dest_value_lat_double);
        ORIGIN = Point.fromLngLat(source_value_long_double, source_value_lat_double);

        Log.d("SOURCE LOCATION", "SOURCELOCATION iS THIS: " + ORIGIN);
        Log.d("DEST LOCATION", "DESTLOCATION iS THIS: " + DESTINATION);

        initLocationEngine();
    }

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        if (locationEngine == null)
            Log.d("TEST", "LocationEngine is null");
        Location lastLocation = locationEngine.getLastLocation();

        if (lastLocation != null) {
            originLocation = lastLocation;
            //setCameraPosition(lastLocation);
            setCameraPosition(lastLocation);
            navigationView.initialize(this);
            fetchRoute();
        } else {
            Log.d("TEST", "lastLocation is null");
            locationEngine.addLocationEngineListener(this);
        }
        Log.d("TEST", "initialize location Engine");
    }


    private void setCameraPosition(Location location) {
        Log.d("TEST", "settingcamera");
        //  map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 1000.0));
    }


    private void fetchRoute() {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(ORIGIN)
                .destination(DESTINATION)
                .profile(DirectionsCriteria.PROFILE_CYCLING)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        try {
                            if (response.body() == null) {
                                Log.e("Test", "No Routes Found,Check right user and access token");
                                return;
                            } else if (response.body().routes().size() == 0) {
                                Log.e("Test", "No Route");
                                return;
                            }
                            DirectionsRoute directionsRoute = response.body().routes().get(0);
                            startNavigation(directionsRoute);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e("Test", "Error" + t.getMessage());
                    }
                });
    }

    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationViewOptions.Builder options =
                NavigationViewOptions.builder()
                        .navigationListener(this)
                        .directionsRoute(directionsRoute);
        navigationView.startNavigation(options.build());
        //navigationView.showRouteInfo(true);
    }


    @Override
    public void onNavigationReady() {
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        navigationView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        if (!navigationView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        navigationView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        navigationView.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        navigationView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCancelNavigation() {
        finish();
    }

    @Override
    public void onNavigationFinished() {
        finish();
    }

    @Override
    public void onNavigationRunning() {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onLocationChanged(Location location) {
    }
}