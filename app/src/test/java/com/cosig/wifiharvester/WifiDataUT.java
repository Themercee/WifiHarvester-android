package com.cosig.wifiharvester;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Jimmy on 2016-06-08.
 */
public class WifiDataUT {

    @Test
    public void wifiData_DiffSSID_TheyAreDiff() throws Exception{
        WifiData wifiOne = new WifiData("SSID","BSSID","WEP");
        WifiData wifiTwo = new WifiData("Other","BSSID","WEP");

        Assert.assertNotEquals(wifiOne.getSSID(),wifiTwo.getSSID());
    }

    @Test
    public void wifiData_DiffBSSID_TheyAreDiff() throws Exception{
        WifiData wifiOne = new WifiData("SSID","BSSID","WEP");
        WifiData wifiTwo = new WifiData("SSID","other","WEP");

        Assert.assertNotEquals(wifiOne.getBSSID(),wifiTwo.getBSSID());
    }

    @Test
    public void wifiData_SameSSID_TheyAreEqual() throws Exception{
        WifiData wifiOne = new WifiData("SSID","BSSID","WEP");
        WifiData wifiTwo = new WifiData("SSID","BSSID","WEP");

        Assert.assertEquals(wifiOne.getSSID(),wifiTwo.getSSID());
    }

    @Test
    public void wifiData_SameBSSID_TheyAreEqual() throws Exception{
        WifiData wifiOne = new WifiData("SSID","BSSID","WEP");
        WifiData wifiTwo = new WifiData("SSID","BSSID","WEP");

        Assert.assertEquals(wifiOne.getBSSID(),wifiTwo.getBSSID());
    }
}
