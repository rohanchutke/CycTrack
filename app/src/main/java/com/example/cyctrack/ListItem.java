package com.example.cyctrack;

// Importing necessary modules

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cyctrack.Model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListItem extends AppCompatActivity {

    // Declaring variables
    ListView listView;
    ListAdapter adapter;
    Button backButton;
    ProgressDialog loading;
    TextView titleSubmissions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout to be full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Casting variables to connect with xml
        setContentView(R.layout.list_item);
        listView = findViewById(R.id.lv_items);
        backButton = findViewById(R.id.btn_back_listitems);
        titleSubmissions = findViewById(R.id.title_submissions);

        // calling getItems function
        getItems();

        // setting on click button to take to weather activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListItem.this, WeatherActivity.class);
                startActivity(i);
            }
        });


        // Getting the text in Spannable String
        String title = titleSubmissions.getText().toString();
        SpannableString sstitle = new SpannableString(title);

        // Changing the font color of the spannable screen with the ui color
        ForegroundColorSpan uicolor1 = new ForegroundColorSpan(getResources().getColor(R.color.test_color));

        // Setting the start and end points to color the text
        sstitle.setSpan(uicolor1, 9, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        titleSubmissions.setText(sstitle);

    }

    // Initialize getItems() with progress dialog box and provide GAS url
    private void getItems() {
        loading = ProgressDialog.show(this, "Loading", "please wait", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxzZJrl-RHe_61AfbWPH22pOSu8X_QXQrHFWQjbfiIWpJLDxP4/exec?action=getItems",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        // Request to access the script and putting it in a request queue
        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    // Creating an Array list which parses over each item
    private void parseItems(String jsonResposnce) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();


        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            // Running a loop over the three columns in sheet and storing it in strings
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String route = jo.getString("route");
                String rating = jo.getString("rating");
                String feedback = jo.getString("feedback");

                // Putting the items in the list for listview display
                HashMap<String, String> item = new HashMap<>();
                item.put("route", route);
                item.put("rating", rating);
                item.put("feedback", feedback);
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Gives the list row view layout with route rating and feedback from xml files
        adapter = new SimpleAdapter(this, list, R.layout.list_item_row,
                new String[]{"route", "rating", "feedback"}, new int[]{R.id.tv_route_final, R.id.tv_rating_final, R.id.tv_feedback_final});

        // Setting the adapter
        listView.setAdapter(adapter);

        // Dismissing the loading dialog box once the list is populated
        loading.dismiss();
    }


}