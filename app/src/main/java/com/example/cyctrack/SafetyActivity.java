package com.example.cyctrack;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cyctrack.Model.Weather;

import java.util.ArrayList;

public class SafetyActivity extends Activity {
    ArrayList<String> selectedItems = new ArrayList<>();

    Button btn_letsgo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_safety);

        btn_letsgo = findViewById(R.id.btnLetsGo);
        ListView chl = findViewById(R.id.checkable_list);
        chl.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        String[] items = {"Helmet", "Lights", "Knee-Pads", "Arm-Guards", "Visibility Jacket"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.rowlayout, R.id.txt_title, items);
        chl.setAdapter(adapter);
        chl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItems.contains(selectedItem)) {
                    selectedItems.remove(selectedItem);
                } else {
                    selectedItems.add(selectedItem);
                }

                if (selectedItems.size() == 5) {
                    btn_letsgo.setEnabled(true);
                    btn_letsgo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(SafetyActivity.this, WeatherActivity.class);
                            startActivity(i);
                        }
                    });
                } else {
                    btn_letsgo.setEnabled(false);
                }
            }
        });
    }

}
