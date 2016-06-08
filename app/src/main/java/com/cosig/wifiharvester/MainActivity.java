package com.cosig.wifiharvester;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;


import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.cosig.wifiharvester.clickListener.WifiInfoOnClickListener;
import com.cosig.wifiharvester.clickListener.WifiStartOnClickListener;
import com.cosig.wifiharvester.clickListener.WifiStopOnClickListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private Debug debug;
    private ArrayList<WifiData> wifiArray;
    private ArrayAdapterWifi adapterListWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        debug = Debug.getDebug(this.getApplicationContext());

        initOnClickListener();  //Init Start Stop Info button
        initSwitchVib();
        initLisWifi();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateWifi(ArrayList<WifiData> wifiDataArrayList){
        // Verify if wifi is already there
        ArrayList<WifiData> wifiToAdd = new ArrayList<>();

        for(int i = 0; i < wifiDataArrayList.size();i++){
            WifiData newWifi = wifiDataArrayList.get(i);
            boolean isFound = false;

            for(int j = 0; j < wifiArray.size(); j++){
                WifiData oldWifi = wifiArray.get(j);

                if(newWifi.isEqualTo(oldWifi)){
                    isFound = true;
                }
            }

            if(!isFound) wifiToAdd.add(newWifi);
        }

        adapterListWifi.clear();
        adapterListWifi.addAll(wifiToAdd);
        adapterListWifi.notifyDataSetChanged();
    }

    public void updateGpsState(String state){
        TextView gpsState = (TextView) findViewById(R.id.textViewGpsStatusEdit);
        gpsState.setText(state);
    }

    public void updateGpsProvider(String provider){
        TextView gpsProvider = (TextView) findViewById(R.id.textViewProviderEdit);
        gpsProvider.setText(provider);
    }

    public void updateLatLon(String lat, String lon){
        TextView latEdit = (TextView) findViewById(R.id.textViewLatEdit);
        latEdit.setText(lat);

        TextView lonEdit = (TextView) findViewById(R.id.textViewLonEdit);
        lonEdit.setText(lon);
    }

    private void initOnClickListener(){
        final MainActivity mainActivity = this;

        WifiGpsHarvester wgh = new WifiGpsHarvester(mainActivity);
        WifiStartOnClickListener startListener = new WifiStartOnClickListener(wgh);
        WifiStopOnClickListener stopListener = new WifiStopOnClickListener(wgh);
        WifiInfoOnClickListener infoListener = new WifiInfoOnClickListener(wgh);


        FloatingActionButton start = (FloatingActionButton) findViewById(R.id.start);
        start.setOnClickListener(startListener);

        FloatingActionButton stop = (FloatingActionButton) findViewById(R.id.stop);
        stop.setOnClickListener(stopListener);

        FloatingActionButton info = (FloatingActionButton) findViewById(R.id.info);
        info.setOnClickListener(infoListener);
    }

    private void initSwitchVib(){
        Switch switchVib = (Switch) findViewById(R.id.switchCanVibrate);
        if(switchVib == null){
            debug.log("MainActivity", "SwitchVib == null!! Can't vibrate");
        }

        switchVib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    debug.vibrate();
                    debug.setCanVibrate(true);
                } else {
                    debug.vibrate();
                    debug.setCanVibrate(false);
                }
            }
        });
    }

    private void initLisWifi(){
        wifiArray = new ArrayList<WifiData>();

        adapterListWifi = new ArrayAdapterWifi(this, wifiArray);
        ListView listView = (ListView) findViewById(R.id.wifi_list);
        listView.setAdapter(adapterListWifi);
    }


}
