package com.example.dell.sleepcare.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BluetoothConnector {
    private static BluetoothConnector uniqueInstatnce;

    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<String, BluetoothGatt> gatts = new HashMap<>();
    private ArrayList<BluetoothGattService> mGattServices = new ArrayList<>();
    private ArrayList<BluetoothGattCharacteristic> mGattCharacteristics = new ArrayList<>();
    private ArrayList<BluetoothGattCharacteristic> mWritableCharacteristics = new ArrayList<>();

    private BluetoothConnector(Context context){
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    };

    public boolean connectBluetooth(final String macAddr){
        Log.i("connectBluetooth() ", "Connecting to " + macAddr);
        if(mBluetoothAdapter == null || macAddr == null){
            Log.e("connectBluetooth()", "BluetoothAdapter no initialized or unspecific address");
            return false;
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(macAddr);

        BluetoothGatt bluetoothGatt = device.connectGatt(context, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                String address = gatt.getDevice().getAddress();
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i("onConnStateChange()", "Attempting to start service discovery:" + gatt.discoverServices());
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("onConnStateChange()", "Disconnected from GATT server."); }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                try {
                    Log.e("캐릭터리스틱: ", new String(characteristic.getValue(), "UTF-8") + characteristic.getUuid().toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    // success, we can communicate with the device
                    Log.d("onServiceDiscovered", "success" + String.valueOf(status));
                            checkGattServices(gatt.getServices(), gatt);
                        } else {
                    Log.d("onServiceDiscovered", "failed");
                    // failure
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.d("characteristic", "onCharacteristc changed에서 받아온 charateristic : "+ characteristic.toString());
            }
        });
        Log.d("connectBluetooth()", "Trying to create a connection");
        gatts.put(macAddr, bluetoothGatt);
        return true;
    }

    public void disconnect(String macAddr) {
        if(mBluetoothAdapter == null){
            Log.d("disconnec","Bluetooth adapter is not initialized");
        }
        BluetoothGatt bluetoothGatt = gatts.get(macAddr);
        if(bluetoothGatt != null){
            bluetoothGatt.disconnect();
            gatts.remove(macAddr);
        }
    }



    private int checkGattServices(List<BluetoothGattService> gattServices, BluetoothGatt gatt) {
        for (BluetoothGattService gattService : gattServices) {
            // Default service info
            Log.d("# GATT Service: ", gattService.toString());

            // Remember service
            mGattServices.add(gattService);

            // Extract characteristics
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                // Remember characteristic
                mGattCharacteristics.add(gattCharacteristic);

                boolean isWritable = isWritableCharacteristic(gattCharacteristic);
                if(isWritable) {
                    Log.d("isWritable", "쓰기가능한 charateristic  UUID :" + gattCharacteristic.getUuid().toString());
                    mWritableCharacteristics.add(gattCharacteristic);
                }

                boolean isReadable = isReadableCharacteristic(gattCharacteristic);
                if(isReadable) {
                    Log.d("isReadable", "읽기가능한 charateristic UUID : "+ gattCharacteristic.getUuid().toString());
                    if(gattCharacteristic.getUuid().toString().equals("ffffffff-ffff-ffff-ffff-fffffffffff2")) {
                        Log.e("읽었냐??", gattCharacteristic.getUuid().toString() + String.valueOf(gatt.readCharacteristic(gattCharacteristic)));
                    }
                }

                if(isNotificationCharacteristic(gattCharacteristic)) {
                    Log.d("Notificah", "노티파이 charateristic  UUID: " + gattCharacteristic.getUuid().toString());

                    setCharacteristicNotification(gattCharacteristic, true);
                    if(isWritable && isReadable) {

                        //mDefaultChar = gattCharacteristic;
                    }
                }
            }
        }

        return mWritableCharacteristics.size();
    }

    private void setCharacteristicNotification(BluetoothGattCharacteristic gattCharacteristic, boolean b) {
    }

    private boolean isWritableCharacteristic(BluetoothGattCharacteristic chr) {
        if(chr == null) return false;

        final int charaProp = chr.getProperties();
        if (((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) |
                (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isReadableCharacteristic(BluetoothGattCharacteristic chr) {
        if(chr == null) return false;

        final int charaProp = chr.getProperties();
        if((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNotificationCharacteristic(BluetoothGattCharacteristic chr) {
        if(chr == null) return false;

        final int charaProp = chr.getProperties();
        if((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static synchronized BluetoothConnector getInstance(Context context) {
        if (uniqueInstatnce == null) {
            uniqueInstatnce = new BluetoothConnector(context);
        }
        return uniqueInstatnce;
    }
}
