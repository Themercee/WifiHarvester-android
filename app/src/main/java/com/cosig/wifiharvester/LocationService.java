package com.cosig.wifiharvester;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.content.ContextCompat;

public class LocationService{

    private static LocationService instance = null;

    private WifiGpsHarvester wgh;

    public Location location;
    private Debug debug;

    public double longitude;
    public double latitude;
    public float accuracy;


    /**
     * Singleton implementation
     * @return
     */
    public static LocationService getLocationManager(Context context, WifiGpsHarvester wgh)     {
        if (instance == null) {
            instance = new LocationService(context, wgh);
        }
        return instance;
    }

    /**
     * Local constructor
     */
    private LocationService( Context context, WifiGpsHarvester wgh )     {
        debug = Debug.getDebug(context);
        this.wgh = wgh;
        this.longitude = 0.0;
        this.latitude = 0.0;
        this.accuracy = 0.0f;
        debug.log("LocationService", "LocationService Created.");
    }



    private void updateCoordinates(Location location){
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.accuracy = location.getAccuracy();
    }



    public void onLocationChanged(Location location) {
        updateCoordinates(location);
        this.location = location;
        wgh.addNewGpsCoord(this.latitude, this.longitude);
    }


    public void onStatusChanged(String provider, int status, Bundle extras) {
        wgh.updateGPSState(status);
        wgh.updateGpsProvider(provider);
    }

    public String getDebugInfo(){
        String info = "";

        if(location != null)
        {
            info += "Lat: " + Double.toString(this.latitude) + " -- " + Double.toString(location.getLatitude()) + "\n";
            info += "Lon: " + Double.toString(this.longitude) + " -- " + Double.toString(location.getLongitude()) + "\n";
        }

        info += "Accuracy: " + Float.toString(this.accuracy);
        return info;
    }

}
