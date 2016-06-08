package com.cosig.wifiharvester;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Jimmy on 2016-05-30.
 */
public class ScanWifiAsync  extends AsyncTask<String,List<ScanResult>, Integer>{
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
            String coor = wgh.getGPSRecording();

            // Level of a Scan Result
            wifiList = wifiManager.getScanResults();
            publishProgress(wifiList);

            for (ScanResult scanResult : wifiList) {
                int level = WifiManager.calculateSignalLevel(scanResult.level, 5);
                wgh.addWifiEntry("{ \"BSSID\":\"" + scanResult.BSSID + "\", \"SSID\": \"" + scanResult.SSID + "\", \"Signal\":" + level + coor + "}");
            }
        }

        return 1;
    }

    @Override
    protected  void onProgressUpdate (List<ScanResult>... wifi){
        if(wifi != null && wifi.length > 0){
            wgh.updateListWifi(wifi[0]);
        }
    }
}
