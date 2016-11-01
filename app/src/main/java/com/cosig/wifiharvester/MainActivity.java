package com.cosig.wifiharvester;


import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;


import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import com.cosig.wifiharvester.clickListener.WifiInfoOnClickListener;
import com.cosig.wifiharvester.clickListener.WifiStartOnClickListener;
import com.cosig.wifiharvester.clickListener.WifiStopOnClickListener;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private Debug debug;
    private ArrayList<WifiData> wifiArray;
    private ArrayAdapterWifi adapterListWifi;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ask permission for dangerous permission (See android's doc)
        if(Build.VERSION.SDK_INT >= 23)
        {
            String[] perms = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        }

        debug = Debug.getDebug(this.getApplicationContext());

        initOnClickListener();  //Init Start Stop Info button
        initSwitchVib();
        initWifiList();

        // Create an instance of GoogleAPIClient.
        buildGoogleApiClient();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        boolean locationAccepted = false;
        boolean storageAccepted = false;

        switch(permsRequestCode){

            case 200:

                locationAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;

                storageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;

                break;

        }

        if(!locationAccepted || !storageAccepted)
        {
            // Print error message
            Popup popup = new Popup("The application will close, you did not accept all permission. SDK: " + Integer.toString(Build.VERSION.SDK_INT));
            popup.show(getFragmentManager(),"");
            //finish();
            //System.exit(0);
        }

    }

    public void updateWifi(ArrayList<WifiData> wifiDataArrayList){
        // Verify if wifi is already there
        ArrayList<WifiData> wifiToAdd = new ArrayList<>();

        for(int i = 0; i < wifiDataArrayList.size();i++){
            WifiData newWifi = wifiDataArrayList.get(i);
            boolean isFound = false;

            for(int j = 0; j < wifiArray.size(); j++){
                WifiData oldWifi = wifiArray.get(j);

                if(newWifi.isEqualTo(oldWifi)){
                    isFound = true;
                }
            }

            if(!isFound) wifiToAdd.add(newWifi);
        }

        adapterListWifi.clear();
        adapterListWifi.addAll(wifiToAdd);
        adapterListWifi.notifyDataSetChanged();
    }

    public void updateGpsState(String state){
        TextView gpsState = (TextView) findViewById(R.id.textViewGpsStatusEdit);
        gpsState.setText(state);
    }

    public void updateGpsProvider(String provider){
        TextView gpsProvider = (TextView) findViewById(R.id.textViewProviderEdit);
        gpsProvider.setText(provider);
    }

    public void updateLatLon(String lat, String lon){
        TextView latEdit = (TextView) findViewById(R.id.textViewLatEdit);
        latEdit.setText(lat);

        TextView lonEdit = (TextView) findViewById(R.id.textViewLonEdit);
        lonEdit.setText(lon);
    }

    private void initOnClickListener(){
        final MainActivity mainActivity = this;

        WifiGpsHarvester wgh = new WifiGpsHarvester(mainActivity);
        WifiStartOnClickListener startListener = new WifiStartOnClickListener(wgh);
        WifiStopOnClickListener stopListener = new WifiStopOnClickListener(wgh);
        WifiInfoOnClickListener infoListener = new WifiInfoOnClickListener(wgh);


        FloatingActionButton start = (FloatingActionButton) findViewById(R.id.start);
        start.setOnClickListener(startListener);

        FloatingActionButton stop = (FloatingActionButton) findViewById(R.id.stop);
        stop.setOnClickListener(stopListener);

        FloatingActionButton info = (FloatingActionButton) findViewById(R.id.info);
        info.setOnClickListener(infoListener);
    }

    private void initSwitchVib(){
        Switch switchVib = (Switch) findViewById(R.id.switchCanVibrate);
        if(switchVib == null){
            debug.log("MainActivity", "SwitchVib == null!! Can't vibrate");
        }

        switchVib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    debug.vibrate();
                    debug.setCanVibrate(true);
                } else {
                    debug.vibrate();
                    debug.setCanVibrate(false);
                }
            }
        });
    }

    private void initWifiList(){
        wifiArray = new ArrayList<WifiData>();

        adapterListWifi = new ArrayAdapterWifi(this, wifiArray);
        final ListView listView = (ListView) findViewById(R.id.wifi_list);
        listView.setAdapter(adapterListWifi);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiData wifiData = (WifiData) parent.getAdapter().getItem(position);
                String message = wifiData.getSSID() + "\n" + wifiData.getBSSID() + "\n" + wifiData.getSecurity();
                Popup popup = new Popup(message);
                popup.show(getFragmentManager(),"");
            }
        });


    }

    /******************
    * GOOGLE PLAY API
    * ****************/
    public synchronized void buildGoogleApiClient(){
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            createLocationRequest();
            onStart();
        }
    }

    public void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected void onStart() {
        debug.log("MainActivity", "In method onStart...");
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        debug.log("MainActivity", "In method onStop...");
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String message = "FAILED TO CONNECT TO GOOGLE PLAY SERVICE: " + connectionResult.getErrorMessage() + " Error Code: " + Integer.toString(connectionResult.getErrorCode()) ;
        debug.log("MainActivity", message);
        Popup popup = new Popup(message);
        popup.show(getFragmentManager(),"");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        double lat = mCurrentLocation.getLatitude();
        double lon = mCurrentLocation.getLongitude();

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        debug.log("MainActivity","Connection suspended. Code: " + Integer.toString(i));
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(7000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public Position getGPlayServicePosition(){
        Double lat = mCurrentLocation.getLatitude();
        Double lon = mCurrentLocation.getLongitude();
        Position position = new Position(lat,lon);
        return position;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        String lat = Double.toString(location.getLatitude());
        String lon = Double.toString(location.getLongitude());

        updateLatLon(lat,lon);
        debug.log("MainActivity","Location changed. Lat: " + lat + " Lon: " + lon);
    }
}
