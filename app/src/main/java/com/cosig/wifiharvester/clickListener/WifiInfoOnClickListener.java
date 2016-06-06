package com.cosig.wifiharvester.clickListener;

import android.view.View;

import com.cosig.wifiharvester.WifiGpsHarvester;

/**
 * Created by Jimmy on 2016-05-30.
 */
public class WifiInfoOnClickListener implements View.OnClickListener {
    private WifiGpsHarvester wgh;

    public WifiInfoOnClickListener(WifiGpsHarvester wgh){
        this.wgh = wgh;
    }


    @Override
    public void onClick(View v) {
        wgh.Debug();
    }
}
