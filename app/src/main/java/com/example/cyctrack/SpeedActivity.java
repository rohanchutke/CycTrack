package com.example.cyctrack;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraConstrainedHighSpeedCaptureSession;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

public class SpeedActivity extends AppCompatActivity implements LocationListener {

    SwitchCompat sw_metric;
    TextView tv_speed;


    @RequiresApi(api = VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_speed);

        sw_metric = findViewById(R.id.sw_metric);
        tv_speed = findViewById(R.id.tv_speed);
        //alertTextView = (TextView) findViewById(R.id.AlertTextView);

        if (Build.VERSION.SDK_INT >= VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            doStuff();
        }
        this.updateSpeed(null);
        sw_metric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpeedActivity.this.updateSpeed(null);
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
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

    private void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "waiting for gps connection", Toast.LENGTH_SHORT).show();
    }

    private void updateSpeed(CLocation location) {
        float mCurrentSpeed = 0;
        long val = 0;
        if (location != null) {
            location.setUseMetricUnits(this.useMetricUnits());
            mCurrentSpeed = location.getSpeed();
        }
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", mCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");
        if (this.useMetricUnits()) {
            tv_speed.setText(strCurrentSpeed + " km/h");
            try {
                val = Long.parseLong(strCurrentSpeed);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
            if (val > 20) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(SpeedActivity.this);
                a_builder.setMessage("Caution! Please Slow Down.")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = a_builder.create();
                alert.setTitle("Alert !!!");
                alert.show();
            }
        } else {
            tv_speed.setText(strCurrentSpeed + "miles/h");
            if (val > 12) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(SpeedActivity.this);
                a_builder.setMessage("Caution! Please Slow Down.")
                        .setCancelable(false)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = a_builder.create();
                alert.setTitle("Alert !!!");
                alert.show();
            }
        }
    }

    private boolean useMetricUnits() {
        return sw_metric.isChecked();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStuff();
            } else {
                finish();
            }
        }
    }
}
