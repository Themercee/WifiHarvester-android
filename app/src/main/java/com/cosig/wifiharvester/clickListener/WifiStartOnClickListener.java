package com.cosig.wifiharvester.clickListener;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.cosig.wifiharvester.WifiGpsHarvester;

/**
 * Created by Jimmy on 2016-05-30.
 */
public class WifiStartOnClickListener implements View.OnClickListener {
    private WifiGpsHarvester wgh;


    public WifiStartOnClickListener(WifiGpsHarvester wgh){
        this.wgh = wgh;
    }

    @Override
    public void onClick(View v) {
        //turn on the indicator
        FloatingActionButton startButton = (FloatingActionButton) v;
        startButton.setRippleColor(Color.WHITE);
        startButton.setImageResource(android.support.design.R.drawable.abc_ic_search_api_mtrl_alpha);
        this.wgh.startWifiRecording();
    }
}
