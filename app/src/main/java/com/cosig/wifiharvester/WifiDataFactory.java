package com.cosig.wifiharvester;

/**
 * Created by Jimmy on 2016-06-08.
 */
public class WifiDataFactory {

    public WifiDataFactory(){

    }

    public WifiData createWifiData(String ssid, String bssid, String security){
        if(ssid.isEmpty()){
            ssid = "hidden";
        }
        if(bssid.isEmpty()){
            bssid = "MAC not found";
        }

        return new WifiData(ssid,bssid, security);

    }
}
