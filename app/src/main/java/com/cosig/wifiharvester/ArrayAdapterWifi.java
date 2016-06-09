package com.cosig.wifiharvester;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jimmy on 2016-06-08.
 */
public class ArrayAdapterWifi extends ArrayAdapter<WifiData> {
    private final Context context;
    private final ArrayList<WifiData> values;

    public ArrayAdapterWifi(Context context, ArrayList<WifiData> values) {
        super(context, R.layout.activity_itemlistview, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WifiData wifiData = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.activity_itemlistview, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        textView.setText(wifiData.getSSID() + " | " + wifiData.getSecurity());

        setSecurityIcon(imageView, wifiData);

        return rowView;
    }

    private void setSecurityIcon(ImageView imageView, WifiData wifiData){
        boolean containWPA = wifiData.getSecurity().contains("WPA");
        boolean containWEP = wifiData.getSecurity().contains("WEP");
        boolean containWPS = wifiData.getSecurity().contains("WPS");


        // Assign icon depending on security
        if(containWPA && !containWEP && !containWPS){
            imageView.setImageResource(R.drawable.ic_ok_green);
        }else if(containWPS && !containWEP){
            imageView.setImageResource(R.drawable.ic_ok_yellow);
        }
        else if(containWEP){
            imageView.setImageResource(R.drawable.ic_ok_red);
        }else{
            imageView.setImageResource(R.drawable.ic_question);
        }
    }

}
