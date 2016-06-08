package com.cosig.wifiharvester;

import android.app.ListActivity;
import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by Jimmy on 2016-06-08.
 */
public class WifiGPSAsyncContainer {
    List<ScanResult> wifiList;
    Position position;

    public WifiGPSAsyncContainer(List<ScanResult> wifiList, Position position){
        this.wifiList = wifiList;
        this.position = position;
    }
}
