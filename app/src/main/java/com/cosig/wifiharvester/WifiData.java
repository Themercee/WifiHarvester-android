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
        return this.security;
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
        return this.BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public boolean isEqualTo(WifiData otherWifiData){
        boolean isEqual = false;

        if(this.getBSSID() == otherWifiData.getBSSID() &&
                this.getSSID() == otherWifiData.getSSID()){
            isEqual = true;
        }

        return isEqual;
    }


}
