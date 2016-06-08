package com.cosig.wifiharvester;

import android.app.ListActivity;
import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by Jimmy on 2016-06-08.
 */
public class WifiGPSAsyncContainer {
    public List<ScanResult> wifiList;
    public Position position;
    public int state;
    public String gpsProvider;

    public WifiGPSAsyncContainer(List<ScanResult> wifiList, Position position){
        this.wifiList = wifiList;
        this.position = position;
        this.state = 0;
        this.gpsProvider = "";
    }

    public WifiGPSAsyncContainer(List<ScanResult> wifiList, Position position, int state, String gpsProvider){
        this.wifiList = wifiList;
        this.position = position;
        this.state = state;
        this.gpsProvider = gpsProvider;
    }
}
