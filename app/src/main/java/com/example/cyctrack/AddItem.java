package com.example.cyctrack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;
import java.util.Map;

public class AddItem extends AppCompatActivity implements View.OnClickListener {


    EditText editName, editFeedback;
    Button btnSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.add_item);
        editName = findViewById(R.id.editName);
        editFeedback = findViewById(R.id.editFeedback);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

    }

    //This is the part where data is transafeered from Your Android phone to Sheet by using HTTP Rest API calls


    private void addItemToSheet() {
        final ProgressDialog loading = ProgressDialog.show(this, "Submitting Review", "Please wait");

        //encrypt these editnames
        final String name = editName.getText().toString().trim();
        final String feedback = editFeedback.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxzZJrl-RHe_61AfbWPH22pOSu8X_QXQrHFWQjbfiIWpJLDxP4/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(AddItem.this, response, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "addItem");
                parmas.put("Name", name);
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


    @Override
    public void onClick(View v) {

        if (v == btnSubmit) {
            addItemToSheet();
            //Define what to do when button is clicked

        }
    }
}
