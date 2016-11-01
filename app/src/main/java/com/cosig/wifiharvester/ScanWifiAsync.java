package com.cosig.wifiharvester;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Jimmy on 2016-05-30.
 */
public class ScanWifiAsync  extends AsyncTask<String,WifiGPSAsyncContainer, Integer>{
    private WifiManager wifiManager;
    private  WifiGpsHarvester wgh;
    private  List<ScanResult> wifiList;


    public ScanWifiAsync(WifiManager wifiManager, WifiGpsHarvester wgh){
        this.wifiManager = wifiManager;
        this.wgh = wgh;
        this.wifiList = null;
    }

    @Override
    protected Integer doInBackground(String... params) {

        while (wgh.getContinueScanning())
        {
            boolean result = wifiManager.startScan();

            try{
                Thread.sleep(3000);
            }catch (InterruptedException e){
                Thread.interrupted();
            }

            Position position = wgh.getGPSRecording();

            String coord = "";
            coord += ", \"Lat\": " + Double.toString(position.lat) + ", \"Lon\": " + Double.toString(position.lon);

            wifiList = wifiManager.getScanResults();

            // Update UI
            publishProgress(new WifiGPSAsyncContainer(wifiList, position));

            for (ScanResult scanResult : wifiList) {
                int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
                wgh.addWifiEntry("{ \"BSSID\":\"" + scanResult.BSSID + "\", \"SSID\": \"" + scanResult.SSID + "\", \"Signal\":" + level + coord + "}");
            }
        }

        return 1;
    }

    @Override
    protected  void onProgressUpdate (WifiGPSAsyncContainer... wifiGPSAsyncContainers){
        List<ScanResult> wifi = wifiGPSAsyncContainers[0].wifiList;
        Position position = wifiGPSAsyncContainers[0].position;

        // Update ListView Wifi
        if(wifi != null && wifi.size() > 0){
            wgh.updateListWifi(wifi);
        }

        //Update position
        if(position.lat != -1 && position.lon != -1){
            wgh.updateLatLonForUI(position);
        }
    }
}
