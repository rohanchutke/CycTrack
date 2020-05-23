package com.example.cyctrack;
// Importing required modules

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    // Declaring back button to go to home activity
    Button backbtn_final_aboutus;
    TextView tv_title, tv_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // casting the button to build connection with xml file
        tv_title = findViewById(R.id.tv1);
        backbtn_final_aboutus = findViewById(R.id.btn_home_aboutus);
        // Getting the text in Spannable String
        String title = tv_title.getText().toString();
        SpannableString sstitle = new SpannableString(title);

        // Changing the font color of the spannable screen with the ui color
        ForegroundColorSpan uicolor1 = new ForegroundColorSpan(getResources().getColor(R.color.test_color_ss));

        // Setting the start and end points to color the text
        sstitle.setSpan(uicolor1, 2, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_title.setText(sstitle);


        tv_desc = findViewById(R.id.tv_desc);
        String desc = tv_desc.getText().toString();

        // Getting the text in Spannable String
        SpannableString ssdesc = new SpannableString(desc);

        // Changing the font color of the spannable screen with the ui color
        ForegroundColorSpan uicolor2 = new ForegroundColorSpan(getResources().getColor(R.color.test_color_ss));
        ForegroundColorSpan uicolor3 = new ForegroundColorSpan(getResources().getColor(R.color.test_color_ss));
        ForegroundColorSpan uicolor4 = new ForegroundColorSpan(getResources().getColor(R.color.test_color_ss));

        // Setting the start and end points to color the text
        ssdesc.setSpan(uicolor2, 31, 39, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssdesc.setSpan(uicolor3, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssdesc.setSpan(uicolor4, 194, 195, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tv_desc.setText(ssdesc);

        //setting on click action on button which takes to home activity
        backbtn_final_aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AboutActivity.this, WeatherActivity.class);
                startActivity(i);
            }
        });
    }
}
