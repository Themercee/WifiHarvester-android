package com.cosig.wifiharvester;

import android.app.FragmentManager;
import android.content.Context;
import android.location.LocationProvider;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jimmy on 2016-05-26.
 * Main module for the application. It works as a Controller patern (God class)
 */
public class WifiGpsHarvester {
    private String dataJson;
    private boolean dataEntryAdded;

    private Context context;
    private FragmentManager fragMan;
    private MainActivity mainActivity;
    private boolean newGpsUpdate;

    private  boolean continueScanning;

    private Debug debug;

    private WifiManager.WifiLock wifiLock;

    public WifiGpsHarvester(MainActivity mainActivity){
        this.dataJson = "{ \"wifiNetworks\":[";
        this.dataEntryAdded = false;

        this.context = mainActivity.getApplicationContext();
        this.fragMan =mainActivity.getFragmentManager();
        this.mainActivity = mainActivity;
        this.debug = Debug.getDebug(this.context);

        this.newGpsUpdate = true;

        this.continueScanning = false;
    }

    public void startWifiRecording() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if(!wifiManager.isWifiEnabled())
        {
            debug.log("WifiGpsHarvester","Error: wifi is not enable!");
            Popup unpop = new Popup("Error: Wifi is not enable!");
            unpop.show(this.fragMan,"NoTag");
            return ;
        }

        this.wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "");
        wifiLock.acquire();

        setContinueScanning(true);
        new ScanWifiAsync(wifiManager,this).execute("");
        debug.log("WifiGpsHarvester", "statWifiRecording...");

    }

    public void stopWifiRecording(){
        if(wifiLock != null)
        {
            wifiLock.release();
            continueScanning = false;

            try{
                Thread.sleep(3000);
            }catch (InterruptedException e){
                Thread.interrupted();
            }

            this.dataJson += "]}";
            writeToFile(this.dataJson);
            wifiLock = null;

            debug.log("WifiGpsHarvester","StopWifiRecording...");
        }


    }

    public void addWifiEntry(String entry){
        String sep = "";
        if(this.dataEntryAdded){
            sep = ",";
        }else{
            this.dataEntryAdded = true;
        }

        if(!entry.isEmpty()){
            this.dataJson += sep + "\n" + entry;
        }
    }

    public void updateListWifi(List<ScanResult> wifiList){

        ArrayList<WifiData> wifiDataArrayList = new ArrayList<>();
        WifiDataFactory wifiDataFactory = new WifiDataFactory();

        for(ScanResult wifi : wifiList){
            wifiDataArrayList.add(wifiDataFactory.createWifiData(wifi.SSID, wifi.BSSID, wifi.capabilities));
        }

        mainActivity.updateWifi(wifiDataArrayList);
    }

    public void setContinueScanning(boolean action){
        this.continueScanning = action;
    }

    public boolean getContinueScanning(){
        return this.continueScanning;
    }

    /*********************
     * GPS SECTION
     ********************/

    public void addNewGpsCoord(double lat, double lon){
        this.newGpsUpdate = true;
        updateLatLonForUI(lat,lon);
    }

    public void updateLatLonForUI(Position position){
        if(position.lat != 0 && position.lon != 0){
            mainActivity.updateLatLon(Double.toString(position.lat),Double.toString(position.lon));
        }
    }

    public void updateLatLonForUI(double lat, double lon){
        if(lat != 0 && lon != 0){
            mainActivity.updateLatLon(Double.toString(lat),Double.toString(lon));
        }
    }

    public Position getGPSRecording(){
        debug.log("WifiGpsHarvester", "In getGPSRecording");
        Position result = new Position(0.0,0.0);

        if(this.newGpsUpdate){
            result = mainActivity.getGPlayServicePosition();
            this.newGpsUpdate = false;
            debug.log("WifiGpsHarvester", "In getGPSRecording...add coord: " + Double.toString(result.lat) + ", " + Double.toString(result.lon));
        }
        return result;
    }

    public void updateGPSState(int state){
        String stateString = "";

        if(state == LocationProvider.AVAILABLE){
            stateString = "Available";
        }else if(state == LocationProvider.TEMPORARILY_UNAVAILABLE){
            stateString = "Temporarily unavailable";
        }else {
            stateString = "Out of service";
        }

        mainActivity.updateGpsState(stateString);
    }

    public void updateGpsProvider(String provider){
        mainActivity.updateGpsProvider(provider);
    }




    /********************
     * UTILITY SECTION
     *******************/

    public void Debug(){
        LocationService locationService = LocationService.getLocationManager(context, this);
        Popup unpop = new Popup(locationService.getDebugInfo());
        unpop.show(fragMan,"NoTag");
    }

    private void writeToFile(String data) {
        try {
            FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory() + "/dataSaved.json", true);
            fw.write(data);
            fw.flush();
            fw.close();
        }
        catch (IOException e) {
            Popup unpop = new Popup("Error!: " + e.toString());
            unpop.show(fragMan,"NoTag");
        }
    }



}
