package com.example.cyctrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonAddItem;
    Button buttonListItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_review);

        buttonAddItem = findViewById(R.id.btn_add_item);
        buttonListItem = findViewById(R.id.btn_list_items);
        buttonAddItem.setOnClickListener(this);
        buttonListItem.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v==buttonAddItem){

            Intent intent = new Intent(getApplicationContext(),AddItem.class);
            startActivity(intent);
        }

        if(v==buttonListItem){

            Intent intent = new Intent(getApplicationContext(), ListItem.class);
            startActivity(intent);
        }

    }
}
