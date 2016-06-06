package com.cosig.wifiharvester;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.support.v4.content.ContextCompat;

public class LocationService implements LocationListener  {

    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    //The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    private final static boolean forceNetwork = false;
    private static boolean isGPSEnabled;
    private  static boolean isNetworkEnabled;
    private static boolean locationServiceAvailable;

    private static LocationService instance = null;

    private WifiGpsHarvester wgh;
    private LocationManager locationManager;
    public Location location;
    private Debug debug;

    private static float accuracyNeeded;

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
        initLocationService(context);
        this.longitude = 0.0;
        this.latitude = 0.0;
        this.accuracyNeeded = 15.0f;
        this.accuracy = 0.0f;
        debug.log("LocationService", "LocationService Created.");
    }



    /**
     * Sets up location service after permissions is granted
     */
    @TargetApi(23)
    private void initLocationService(Context context) {
        debug.log("LocationService", "Initialize LocationService.");

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            debug.log("LocationService", "No valid permission!");
            return  ;
        }

        try   {
            this.longitude = 0.0;
            this.latitude = 0.0;
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (forceNetwork) isGPSEnabled = false;

            if (!isNetworkEnabled && !isGPSEnabled)    {
                // cannot get location
                this.locationServiceAvailable = false;
                debug.log("LocationService", "Network and GPS not available.");
            }
            //else
            {
                this.locationServiceAvailable = true;

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null)   {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateCoordinates(location);
                    }
                }//end if

                if (isGPSEnabled)  {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null)  {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        updateCoordinates(location);
                    }
                }
            }
        } catch (Exception ex)  {
            debug.log("LocationService", "CATCH ERROR: " + ex.getMessage());
        }
    }

    private void updateCoordinates(Location location){
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.accuracy = location.getAccuracy();
    }


    @Override
    public void onLocationChanged(Location location) {
        updateCoordinates(location);
        this.location = location;
        wgh.addNewGpsCoord(this.latitude, this.longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        wgh.updateGPSState(status);
        wgh.updateGpsProvider(provider);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public String getDebugInfo(){
        String info = "";

        if(location != null)
        {
            info += "Lat: " + Double.toString(this.latitude) + " -- " + Double.toString(location.getLatitude()) + "\n";
            info += "Lon: " + Double.toString(this.longitude) + " -- " + Double.toString(location.getLongitude()) + "\n";
        }

        info += "GPS enable: " + Boolean.toString(isGPSEnabled) + "\n";
        info += "Network enable: " + Boolean.toString(isNetworkEnabled) + "\n";
        info += "Accuracy: " + Float.toString(this.accuracy);
        return info;
    }

    public void changeAccuracyNeeded(float accuracy){
        this.accuracyNeeded = accuracy;
    }
}
