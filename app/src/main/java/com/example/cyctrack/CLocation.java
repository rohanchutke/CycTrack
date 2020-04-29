package com.example.cyctrack;

import android.location.Location;

import java.sql.Struct;

public class CLocation extends Location {

    private boolean bUseMetricUnits = false;

    public CLocation(Location location) {
        this(location, true);
    }

    public CLocation(Location location, boolean bUseMetricUnits) {
        super(location);
        this.bUseMetricUnits = bUseMetricUnits;
    }

    public boolean getUseMetricUnits() {
        return this.bUseMetricUnits;
    }

    public void setUseMetricUnits(boolean bUseMetricUnits) {
        this.bUseMetricUnits = bUseMetricUnits;
    }

    @Override
    public float distanceTo(Location dest) {
        float nDistance = super.distanceTo(dest);
        if (!this.getUseMetricUnits()) {
            nDistance = nDistance * 3.2808f;
        }
        return nDistance;
    }

    @Override
    public double getAltitude() {
        double nAltitude = super.getAltitude();
        if (!this.getUseMetricUnits()) {
            nAltitude = nAltitude * 3.2808d;
        }
        return nAltitude;
    }

    @Override
    public float getSpeed() {
        float nSpeed = super.getSpeed() * 3.6f;
        if (!this.getUseMetricUnits()) {
            nSpeed = super.getSpeed() * 2.2369f;
        }
        return nSpeed;
    }

    @Override
    public float getAccuracy() {
        float nAccuracy = super.getAccuracy();
        if (!this.getUseMetricUnits()) {
            nAccuracy = nAccuracy * 3.2808f;
        }
        return nAccuracy;
    }
}
