package com.example.cyctrack;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListItem extends AppCompatActivity {


    ListView listView;
    ListAdapter adapter;
    ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.list_item);
        listView = findViewById(R.id.lv_items);
        getItems();
    }


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

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }


    private void parseItems(String jsonResposnce) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String name = jo.getString("Name");
                String feedback = jo.getString("feedback");

                HashMap<String, String> item = new HashMap<>();
                item.put("Name", name);
                item.put("feedback", feedback);
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new SimpleAdapter(this, list, R.layout.list_item_row,
                new String[]{"Name", "feedback"}, new int[]{R.id.tv_item_name, R.id.tv_brand});

        listView.setAdapter(adapter);
        loading.dismiss();
    }


}