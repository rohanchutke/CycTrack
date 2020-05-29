package com.example.cyctrack;

// Importing necessary modules

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cyctrack.Common.Common;
import com.example.cyctrack.Helper.Helper;
import com.example.cyctrack.Model.OpenWeatherMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

public class WeatherActivity extends AppCompatActivity implements LocationListener {

    // Declaring variables
    TextView txtCity, txtDescription, txtHumidity, txtCelsius, tvSettext, tvTitle;
    Button btn_maps, btn_feedback, btn_Aboutus;
    ImageView imageView;

    LocationManager locationManager;
    String provider;
    static double lat, lng;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();

    int MY_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_weather);

        // Casting variables for connection with xml file
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);
        tvTitle = findViewById(R.id.tv_title_weather);
        imageView = (ImageView) findViewById(R.id.imageView);
        tvSettext = findViewById(R.id.tv_settext);
        btn_Aboutus = findViewById(R.id.tv_aboutus);
        btn_feedback = findViewById(R.id.btn_feedback);
        btn_maps = findViewById(R.id.btn_maps1);

        // Get access to location service
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        // Permissions asked in the manifest file for location , network and external storage
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);

        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null)
            Log.e("TAG", "No Location");


        // If clicked on maps button go to maps activity
        btn_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WeatherActivity.this, SafetyActivity.class);
                startActivity(i);
            }
        });

        // If clicked on feedback button go to view feedback activity
        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WeatherActivity.this, ListItem.class);
                startActivity(i);
            }
        });


        // If clicked on About us button go to  About us activity
        btn_Aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WeatherActivity.this, AboutActivity.class);
                startActivity(i);
            }
        });

        String title = tvTitle.getText().toString();
        SpannableString sstitle = new SpannableString(title);
        ForegroundColorSpan uicolor1 = new ForegroundColorSpan(getResources().getColor(R.color.test_color_ss));
        sstitle.setSpan(uicolor1, 4, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTitle.setText(sstitle);


    }

    // ASk permission for location services
    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }
        locationManager.removeUpdates(this);
    }

    // When it is resumed, again ask for permissions for location,network, and storage
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WeatherActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    // When location changes get the longitude and latitude of the new location
    @Override
    public void onLocationChanged(Location location) {

        lat = location.getLatitude();
        lng = location.getLongitude();


        // Get api data from new location obtained for latitude and longitude
        new GetWeather().execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lng)));

    }

    //predefined function
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    //predefined function
    @Override
    public void onProviderEnabled(String provider) {

    }

    //predefined function
    @Override
    public void onProviderDisabled(String provider) {

    }

    // Before executing to get weather, set a process dialog box for users to know that it is loading data
    private class GetWeather extends AsyncTask<String, Void, String> {

//         ProgressDialog pd = new ProgressDialog(WeatherActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pd.setTitle("Please wait...");
            //pd.show();
        }

        // While the dialog box is on, process the data in the background
        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return stream;
        }


        // Once the data is retrieved, display city, description, humidity, temperature and textview
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            txtCity.setVisibility(1);
            txtDescription.setVisibility(1);
            txtHumidity.setVisibility(1);
            imageView.setVisibility(1);
            txtCelsius.setVisibility(1);
            tvSettext.setVisibility(1);

            // Throw an error city no found if it fails
            if (s.contains("Error: Not found city")) {
                // pd.dismiss();
                return;
            }

            // Use gson to get json data from api
            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>() {
            }.getType();
            openWeatherMap = gson.fromJson(s, mType);
            //pd.dismiss();

            // Setting city string to the current city based on infromation receieved from OPen weatherMap
            txtCity.setText(String.format("%s,%s", openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));

            //// Setting description string of the weather condition based on infromation receieved from OPen weatherMap
            txtDescription.setText(String.format("%s", openWeatherMap.getWeather().get(0).getDescription()));

            // If current weather description is clear sky or few clouds, set textview to good day to ride
            if (txtDescription.getText().toString().matches("clear sky") || txtDescription.getText().toString().matches("few clouds")) {
                tvSettext.setText("It is SAFE to ride today !!");
                String title = tvSettext.getText().toString();
                SpannableString sstitle = new SpannableString(title);
                ForegroundColorSpan uicolor1 = new ForegroundColorSpan(getResources().getColor(R.color.test_color_ss));
                sstitle.setSpan(uicolor1, 5, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvSettext.setText(sstitle);

                // If current weather condition is anything else then set textview as not a good day to ride
            } else {
                tvSettext.setText("It is UNSAFE to ride today !!");
                String title1 = tvSettext.getText().toString();
                SpannableString sstitle1 = new SpannableString(title1);
                ForegroundColorSpan uicolor2 = new ForegroundColorSpan(getResources().getColor(R.color.test_color_ss));
                sstitle1.setSpan(uicolor2, 5, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvSettext.setText(sstitle1);
            }

            //// Setting humidity string of the weather condition based on infromation receieved from OPen weatherMap
            String humidity_string = "Humidity: ";
            txtHumidity.setText(humidity_string + String.format("%d%%", openWeatherMap.getMain().getHumidity()));

            // Setting temperature string of the weather condition based on infromation receieved from OPen weatherMap
            txtCelsius.setText(String.format("%.2f °C", openWeatherMap.getMain().getTemp()));

            // Load image from OpenWeather MAp based on the description of current weather condition
            Picasso.get()/*
                    .load(new StringBuilder("https://openweathermap.org/img/wn/").append(openWeatherMap.getWeather().get(0).getIcon())
                            .append("@2x.png").toString())
                    .into(imageView);*/
            .load(new StringBuilder("https://openweathermap.org/img/w/").append(openWeatherMap.getWeather().get(0).getIcon())
                    .append(".png").toString())
                    .into(imageView);

        }

    }
}
