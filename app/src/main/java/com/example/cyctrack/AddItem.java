package com.example.cyctrack;

// Importing required modules

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cyctrack.Model.Main;
import com.example.cyctrack.Model.Weather;

import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity implements View.OnClickListener {

    // Declaring variables
    EditText editName, editFeedback;
    Button btnSubmit;
    TextView tvRating, tv_source_Route, tv_dest_Route, tv_title;
    private RatingBar ratingBar;
    private float ratedValue;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Casting variables for connection with xml file
        setContentView(R.layout.add_item);
        editName = findViewById(R.id.editName);
        editFeedback = findViewById(R.id.editFeedback);
        btnSubmit = findViewById(R.id.btnSubmit);
        tv_title = findViewById(R.id.tv_title);
        tv_source_Route = findViewById(R.id.tv_source_route);
        tv_dest_Route = findViewById(R.id.tv_dest_route);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        //Getting the rating value in float as given by the user
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override

            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratedValue = ratingBar.getRating();
            }
        });

        // Making button clickable
        btnSubmit.setOnClickListener(this);

        // tvRoute.setText(getIntent().getStringExtra("destionation_key"));
        // Using shared preferences to get String values of source and destination address to review the route
        SharedPreferences source_result = getSharedPreferences("Source_key", Context.MODE_PRIVATE);
        String source_value = source_result.getString("Source_key_value", "Data Not Found");
        tv_source_Route.setText(source_value);

        SharedPreferences destination_result = getSharedPreferences("Destination_key", Context.MODE_PRIVATE);
        String destination_value = destination_result.getString("Destination_key_value", "Data Not Found");
        tv_dest_Route.setText(destination_value);

        // Getting the text in Spannable String
        String title = tv_title.getText().toString();
        SpannableString sstitle = new SpannableString(title);

        // Changing the font color of the spannable screen with the ui color
        ForegroundColorSpan uicolor1 = new ForegroundColorSpan(getResources().getColor(R.color.test_color));

        // Setting the start and end points to color the text
        sstitle.setSpan(uicolor1, 6, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_title.setText(sstitle);

    }


    //This is the part where data is transferred from Your Android phone to Sheet by using HTTP Rest API calls
    private void addItemToSheet() {
        final ProgressDialog loading = ProgressDialog.show(this, "Submitting Review", "Please wait");

        //encrypt these editnames
        final String name = editName.getText().toString().trim();
        final String feedback = editFeedback.getText().toString().trim();
        final String rating = Float.toString(ratedValue);
        final String source = tv_source_Route.getText().toString().trim();
        final String dest = tv_dest_Route.getText().toString().trim();
        final String route = source + " - " + dest;

        //Post method in Google Apps Script  and the url to access the google script
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxzZJrl-RHe_61AfbWPH22pOSu8X_QXQrHFWQjbfiIWpJLDxP4/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(AddItem.this, response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            //HashMap to put fields from front end to Google Apps Script
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "addItem");
                parmas.put("Name", name);
                parmas.put("route", route);
                parmas.put("rating", rating);
                parmas.put("feedback", feedback);
                return parmas;
            }
        };
        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    // If clicked on button Submit, adding items to Google Sheet
    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            addItemToSheet();
            //Define what to do when button is clicked
        }
    }
}
