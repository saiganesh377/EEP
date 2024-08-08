package com.example.gps;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.Manifest;
import androidx.core.app.ActivityCompat;
public class LocationUpdater {
    FusedLocationProviderClient fused;
    private static String TAG = "dummy";
    LocationRequest locreq = new LocationRequest();


    public void setLow(){
        locreq.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        Log.d(TAG,"Saving Power");
    }
    public void setHigh(){
        locreq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d(TAG,"High accuracy");
    }

    public void setinterval(){
        locreq.setInterval(10000);
    }

    private void LogLocation(Location location){
        Log.d(TAG, String.valueOf(location.getAccuracy()));
        Log.d(TAG, String.valueOf(location.getLatitude()));
        Log.d(TAG, String.valueOf(location.getLongitude()));

    }

    public void updateGPS(Context context) {
        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "inside if");
            fused = LocationServices.getFusedLocationProviderClient(context);

            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        Log.d(TAG, "Location update");
                        LogLocation(location);
                    }
                }
            };

            // Set the location update interval to 5 seconds (5000 milliseconds)
            locreq.setInterval(5000);
            locreq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            fused.requestLocationUpdates(locreq, locationCallback, null);
        }

}}
