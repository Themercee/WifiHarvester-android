package com.cosig.wifiharvester;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Jimmy on 2016-05-30.
 */
public class ScanWifiAsync  extends AsyncTask<String,Void,String>{
    private WifiManager wifiManager;
    private  WifiGpsHarvester wgh;


    public ScanWifiAsync(WifiManager wifiManager, WifiGpsHarvester wgh){
        this.wifiManager = wifiManager;
        this.wgh = wgh;
    }

    @Override
    protected String doInBackground(String... params) {

        while (wgh.getContinueScanning())
        {
            boolean result = wifiManager.startScan();

            try{
                Thread.sleep(3000);
            }catch (InterruptedException e){
                Thread.interrupted();
            }
            String coor = wgh.getGPSRecording();

            // Level of a Scan Result
            List<ScanResult> wifiList = wifiManager.getScanResults();
            for (ScanResult scanResult : wifiList) {
                int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
                wgh.addWifiEntry("{ \"BSSID\":\"" + scanResult.BSSID + "\", \"SSID\": \"" + scanResult.SSID + "\", \"Signal\":" + level + coor + "}");
            }
        }

        return null;
    }
}
