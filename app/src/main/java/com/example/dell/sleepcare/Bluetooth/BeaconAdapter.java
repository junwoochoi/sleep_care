package com.example.dell.sleepcare.Bluetooth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.example.dell.sleepcare.R;

import java.util.ArrayList;

public class BeaconAdapter extends BaseAdapter {


    private ArrayList<BleDevice> beacons;
    private LayoutInflater layoutInflater;


    public BeaconAdapter(ArrayList<BleDevice> beacons, LayoutInflater layoutInflater) {
        this.beacons = beacons;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return beacons.size();
    }

    @Override
    public Object getItem(int position) {
        return beacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BeaconHolder beaconHolder;

        if (convertView == null) {
            notifyDataSetChanged();
            beaconHolder = new BeaconHolder();
            convertView = layoutInflater.inflate(R.layout.item_beacon, parent, false);
            beaconHolder.address = convertView.findViewById(R.id.address);
            beaconHolder.name = convertView.findViewById(R.id.text_bt_device_name);


            convertView.setTag(beaconHolder);
        } else {
            beaconHolder = (BeaconHolder)convertView.getTag();
        }
        String macAddr = beacons.get(position).getMac();
        beaconHolder.address.setText("MAC Addr :"+macAddr);
        String deviceName;
        if(beacons.get(position).getName() == null) {
            deviceName = "디바이스 이름: "+"Unknown";
        } else {
            deviceName = "디바이스 이름: "+beacons.get(position).getName();
        }
        beaconHolder.name.setText(deviceName) ;
        return convertView;
    }

    private class BeaconHolder {
        TextView address;
        TextView name;



    }
}