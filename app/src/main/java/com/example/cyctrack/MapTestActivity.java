package com.example.cyctrack;
// Importing necessary modules

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Extending Main class with all Location listeners, Permission checks and on Map call
public class MapTestActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationEngineListener, PermissionsListener, View.OnClickListener, LocationListener, NavigationListener {

    // Delcaring variables
    private PermissionsManager permissionsManager;
    private MapView mapView;
    private Button startButton;
    private MapboxMap map;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private NotificationManager mNotificationManager;
    private Location lastlocation;
    private Point originPosition;
    private Point destinationPosition;
    private NavigationMapRoute navigationMapRoute;
    private NavigationListener navigationListener;
    private SharedPreferences sharedPreferences;
    private static final String TAG = "MainActivity";
    private String TEST = "NAVI_TEST";
    private EditText edt_search;
    private Button btn_submit, btn_home, btn_review;
    private TextView tv_speedtest, tv_ballon;
    private SwitchCompat tglbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // To get instance with help of Mapbox access token
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_map_test);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Casting variables to map to xml file
        mapView = findViewById(R.id.mapView);
        startButton = findViewById(R.id.startbutton);
        btn_home = findViewById(R.id.homeBtn);
        btn_review = findViewById(R.id.btn_review_route);
        edt_search = findViewById(R.id.edt_address);
        tglbtn = findViewById(R.id.tglNew);
        tv_speedtest = findViewById(R.id.tv_speed_latest);
        tv_ballon = findViewById(R.id.tv_ballon);
        btn_submit = findViewById(R.id.btn_submit);

        // Getting access to Notification Service
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Setting on click listener on all the buttons on the Map screen
        startButton.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_review.setOnClickListener(this);
        tv_ballon.setOnClickListener(this);


        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MapTestActivity.this, WeatherActivity.class);
                startActivity(i);
            }
        });

        // Implementing a toggle button for DND functionality
        tglbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // If the toggle button is checked, then access notification manager to turn on DND Mode
                if (isChecked) {
                    changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_NONE);
                    Toast.makeText(MapTestActivity.this, "DND Enabled!", Toast.LENGTH_SHORT).show();


                } else {

                    // If the toggle button isnt check, then interrupt accessing the notification manager
                    changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_ALL);
                    Toast.makeText(MapTestActivity.this, "DND Disabled!", Toast.LENGTH_SHORT).show();

                }
            }
        });
        //added because new access fine location policies, imported class..
        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        //check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            //start the program if permission is granted
            doStuff();
        }
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            // If the edit text to enter destination address is empty set usability of the buttons as false
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    btn_review.setEnabled(true);
                    btn_submit.setEnabled(true);
                }
            }

            // Keep it editable as user can wipe out the data
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Underlining the About us Textview
        tv_ballon.setPaintFlags(tv_ballon.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Using ballon library to show a pop up When user clicks on DND mode to explain its functionality
        Balloon balloon = new Balloon.Builder(getApplicationContext())
                .setArrowSize(15)
                .setArrowOrientation(ArrowOrientation.BOTTOM)
                .setArrowVisible(true)
                .setWidthRatio(0.8f)
                .setHeight(100)
                .setTextSize(15f)
                .setArrowPosition(0.45f)
                .setCornerRadius(4f)
                .setAlpha(0.9f)
                .setText("This is Do Not Disturb mode. All the incoming calls will be silenced.")
                .setTextColor(ContextCompat.getColor(this, R.color.white))
                .setBackgroundColor(ContextCompat.getColor(this, R.color.ui_color))
                .setBalloonAnimation(BalloonAnimation.FADE)
                .build();


        // Setting the ballon on when clicked for 2 sec
        tv_ballon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balloon.show(tv_ballon);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        balloon.dismiss();
                    }
                }, 2000);
            }
        });


    }

    //Get access to location service
    private void doStuff() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            // Request GPS provider for location updates everytime the location changes
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            //commented, this is from the old version
            // this.onLocationChanged(null);
        }
        Toast.makeText(this, "Waiting for GPS connection!", Toast.LENGTH_SHORT).show();
    }

    // CHecking for minimum api level and then granting access
    protected void changeInterruptionFiler(int interruptionFilter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // If api level minimum 23
            if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                mNotificationManager.setInterruptionFilter(interruptionFilter);
            } else {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    //Get in string the address typed in edit text
    private void show() {
        String destination = edt_search.getText().toString();

        // Using shared preferences to share the data retrieved from address bar to Add Item class using key value pair
        sharedPreferences = getSharedPreferences("Destination_key", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Destination_key_value", destination);
        editor.apply();

        // Using Geocoder to get the coordinates from the Location Name( latitude and longitude)
        Geocoder mGeocoder = new Geocoder(getApplicationContext());
        try {
            List<Address> mResultLocation = mGeocoder.getFromLocationName(destination, 1);
            double latitude = mResultLocation.get(0).getLatitude();
            double longitude = mResultLocation.get(0).getLongitude();

            Log.d("Address", "Destination Address: " + destination);

            // Putting latitude and longitude in one point
            destinationPosition = Point.fromLngLat(longitude, latitude);

            //saving destination latitude point into shared preferences
            sharedPreferences = getSharedPreferences("Destination_lat_for_nav", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_dest_lat = sharedPreferences.edit();
            editor_dest_lat.putString("Destination_lat_for_nav_value", String.valueOf(latitude));
            editor_dest_lat.apply();

            //saving destination longitude point into shared preferences
            sharedPreferences = getSharedPreferences("Destination_long_for_nav", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_dest_long = sharedPreferences.edit();
            editor_dest_long.putString("Destination_long_for_nav_value", String.valueOf(longitude));
            editor_dest_long.apply();

            // Putting latitude and longitude in one point for origin using get function
            originPosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());

            //saving source point into shared preferences
            sharedPreferences = getSharedPreferences("Source_lat_for_nav", Context.MODE_PRIVATE);

            //saving source latitude point into shared preferences
            SharedPreferences.Editor editor_source_lat = sharedPreferences.edit();
            editor_source_lat.putString("Source_lat_for_nav_value", String.valueOf(originLocation.getLatitude()));
            editor_source_lat.apply();

            //saving source longitude point into shared preference
            sharedPreferences = getSharedPreferences("Source_long_for_nav", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor_source_long = sharedPreferences.edit();
            editor_source_long.putString("Source_long_for_nav_value", String.valueOf(originLocation.getLongitude()));
            editor_source_long.apply();

            // Getting the route using origin point and destination point
            getRoute(originPosition, destinationPosition);

            // Enabling start button once the route is traced
            startButton.setEnabled(true);

            // Setting camera position once dstination point is set
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom(15)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 2000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // getting the geocodes by taking in the address of the location
        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken(Mapbox.getAccessToken())
                .query(originPosition)
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build();

        // USing reverse geocode function
        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();
                if (results.size() > 0) {

                    // Accessing the index place 0 to get the short and complete place Name
                    String source_address_complete = response.body().features().get(0).placeName();
                    String source_address_short = response.body().features().get(0).text();

                    // Using shared preferences to share the source location
                    sharedPreferences = getSharedPreferences("Source_key", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("Source_key_value", source_address_short);
                    editor.apply();

                    // Checking for short and complete source address by logcat
                    Point firstResultPoint = results.get(0).center();
                    //Log.d(TAG, "onResponse: " + firstResultPoint.coordinates());
                    Log.d(TAG, "onResponse: " + source_address_short);
                    Log.d(TAG, "onResponse: " + source_address_complete);
                } else {
                    Log.d(TAG, "onResponse: No result found");
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    // Making Map box ready to go by enabling location
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        Log.d(TEST, "onMapReady");
        map = mapboxMap;
        enableLocation();
    }

    // Asking permission for location access
    private void enableLocation() {
        Log.d(TEST, "enableLocation");
        if (permissionsManager.areLocationPermissionsGranted(this)) {
            Log.d(TEST, "LocationPermission Ok");
            initializeLocationEngine();
            initializeLocationLayer();
        } else {
            Log.d(TEST, "LocationPermission NO");
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
        Log.d(TAG, "enable Location");
    }

    // Get the best accuracy location by activating location engine
    @SuppressWarnings("MissingPermission")
    private void initializeLocationEngine() {
        Log.d(TEST, "initializeLocationEngine");
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();


        // If location engine is null set it to last known location
        if (locationEngine == null)
            Log.d(TEST, "LocationEngine is null");
        Location lastLocation = locationEngine.getLastLocation();

        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            Log.d(TEST, "lastLocation is null");
            locationEngine.addLocationEngineListener(this);
        }
        Log.d(TAG, "initialize location Engine");
    }

    // Setting up the camera by enabling location layer
    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer() {
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }


    private void setCameraPosition(Location location) {
        Log.d(TAG, "settingcamera");
        //  map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 1000.0));
    }


    // here the navigation starts by taking in source and destination points
    private void getRoute(Point origin, Point destination) {

        // Drawing a route from source to dest
        NavigationRoute.builder()

                // Accessing the token
                .accessToken(Mapbox.getAccessToken())

                // Setting source and dest
                .origin(origin).destination(destination)

                // Building the route
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                        // If no response display no route found
                        if (response.body() == null) {
                            Log.e(TAG, "No Routes Found,Check right user and access token");
                            return;
                        } else if (response.body().routes().size() == 0) {
                            Log.e(TAG, "No Route");
                            return;
                        }

                        // Or else access zeroth index of response route and add route to navigation builder
                        DirectionsRoute currentRoute = response.body().routes().get(0);
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error" + t.getMessage());
                    }

                });
    }

    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    // Everytime the location changes update current location as user location
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            Log.d(TAG, "on location changed");
            //  setCameraPosition(location);
        }

        // Setting the medial player for alert sound
        final MediaPlayer speedAlertPlayer = MediaPlayer.create(MapTestActivity.this, R.raw.overspeedalert);
        if (location == null) {

            // If location is null, set speed as -.-
            tv_speedtest.setText("-.- km/h");
        } else {

            // Else calculate the current speed
            float nCurrentSpeed = location.getSpeed() * 3.6f;

            // Set the current speed to a text view
            tv_speedtest.setText(String.format("%.2f", nCurrentSpeed) + " km/h");
            //Toast.makeText(this, "Current Speed is: " + (String.format("%.2f", nCurrentSpeed)), Toast.LENGTH_SHORT).show();

            // If current speed is greater than 30 kmph
            if (nCurrentSpeed > 30.0) {

                // play the alert sound
                speedAlertPlayer.start();

                // Inflater used to set a toast outside its current activity and can be customized
                LayoutInflater inflater = getLayoutInflater();

                // for speed greater than 30, set toast as red color
                View layout = inflater.inflate(R.layout.custom_toast_red,
                        (ViewGroup) findViewById(R.id.toast_layout_red));
                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText((String.format("%.1f", nCurrentSpeed)));

                // Adjusting the position of the toast on the screen
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER | Gravity.RIGHT, 30, -110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                // If speed is between 10 and 30 set the color of toast to green
            } else if (nCurrentSpeed > 10 && nCurrentSpeed <= 30) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast,
                        (ViewGroup) findViewById(R.id.toast_layout));

                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText((String.format("%.1f", nCurrentSpeed)));

                // adjusting toast position on the screen
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER | Gravity.RIGHT, 30, -110);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            }
        }
    }

    // Implemented default methods of navigation
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    // setting onclick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit: {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                show();
                break;
            }
            case R.id.startbutton: {
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .origin(originPosition)
                        .destination(destinationPosition)
                        .directionsProfile(DirectionsCriteria.PROFILE_CYCLING)
                        .shouldSimulateRoute(false)
                        .build();
                NavigationLauncher.startNavigation(MapTestActivity.this, options);
                //  Intent i = new Intent(MapTestActivity.this, MapNavActivity.class);
                //  startActivity(i);
                break;
            }

            case R.id.btn_review_route: {
                Intent i = new Intent(MapTestActivity.this, AddItem.class);
                startActivity(i);

            }
        }
    }

    // Put a toast that app needs permission
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    // If permission granted, enable location
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation();
        }
    }

    // Calling the doStuff function
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doStuff();
    }

    // Starts the map view
    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }


    // Resumes the map view
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    // Holds the mapview on pause
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    // Stops the map functionality
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    // save the current instance of map
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    // Throws an error of low memory
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // Destroying the navigation window
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mapView.onDestroy();
    }

    //predefined functions of Navigation
    @Override
    public void onCancelNavigation() {
        finish();
    }

    //predefined functions of Navigation
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //predefined functions of Navigation
    @Override
    public void onNavigationFinished() {
        finish();
    }

    //predefined functions of Navigation
    @Override
    public void onNavigationRunning() {
    }
}
