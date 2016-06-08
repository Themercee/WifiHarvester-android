package com.cosig.wifiharvester;

/**
 * Created by Jimmy on 2016-06-07.
 */
public class WifiData {
    private String SSID;
    private String BSSID;
    private String security;

    public WifiData(String ssid, String bssid, String security){
        this.SSID = ssid;
        this.BSSID = bssid;
        this.security = security;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }


}
