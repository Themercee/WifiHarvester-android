package com.cosig.wifiharvester.clickListener;

import android.view.View;

import com.cosig.wifiharvester.WifiGpsHarvester;

/**
 * Created by Jimmy on 2016-05-30.
 */
public class WifiStopOnClickListener implements View.OnClickListener {
    private WifiGpsHarvester wgh;

    public WifiStopOnClickListener(WifiGpsHarvester wgh){
        this.wgh = wgh;
    }

    @Override
    public void onClick(View v) {
        //turn off the indicator
        wgh.stopWifiRecording();
    }
}
