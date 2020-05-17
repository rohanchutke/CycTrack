package com.example.cyctrack;
//import necessary modules
import android.location.Location;

import java.sql.Struct;

public class CLocation extends Location {

    private boolean bUseMetricUnits = false;
    // Use metrics for calculating speed
    public CLocation(Location location) {
        this(location, true);
    }
    // Based on location use metrics
    public CLocation(Location location, boolean bUseMetricUnits) {
        super(location);
        this.bUseMetricUnits = bUseMetricUnits;
    }
    // Returns user selected metrics
    public boolean getUseMetricUnits() {
        return this.bUseMetricUnits;
    }

    public void setUseMetricUnits(boolean bUseMetricUnits) {
        this.bUseMetricUnits = bUseMetricUnits;
    }
    //Calculates the distance based on location
    @Override
    public float distanceTo(Location dest) {
        float nDistance = super.distanceTo(dest);
        if (!this.getUseMetricUnits()) {
            nDistance = nDistance * 3.2808f;
        }
        return nDistance;
    }
    // Calculates the altitude
    @Override
    public double getAltitude() {
        double nAltitude = super.getAltitude();
        if (!this.getUseMetricUnits()) {
            nAltitude = nAltitude * 3.2808d;
        }
        return nAltitude;
    }
    // Calculates the speed
    @Override
    public float getSpeed() {
        float nSpeed = super.getSpeed() * 3.6f;
        if (!this.getUseMetricUnits()) {
            nSpeed = super.getSpeed() * 2.2369f;
        }
        return nSpeed;
    }
    // Calculates the accuracy
    @Override
    public float getAccuracy() {
        float nAccuracy = super.getAccuracy();
        if (!this.getUseMetricUnits()) {
            nAccuracy = nAccuracy * 3.2808f;
        }
        return nAccuracy;
    }
}
