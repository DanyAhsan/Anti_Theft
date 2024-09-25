package com.atexcode.antitheft.lib;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import com.atexcode.antitheft.MainActivity;
import com.atexcode.antitheft.RegisterActivity;

public class AtexGPS {


    private Context context;
    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public AtexGPS(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    // Method to get the current GPS coordinates as a string
    public String getGPSCoordinates() {

            Location lastKnownLocation = getLastKnownLocation();
            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                return String.format("%.4f, %.4f", latitude, longitude);
            } else {
                return "Location not available";
            }

    }

    // Helper method to get the last known location
    private Location getLastKnownLocation() {
        Location location = null;

            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }


        return location;
    }
    }
