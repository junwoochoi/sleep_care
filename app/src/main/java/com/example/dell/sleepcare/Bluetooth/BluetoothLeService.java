package com.example.dell.sleepcare.Bluetooth;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.Utils.Constants;

public class BluetoothLeService extends Service {

    final String BLE_LOG = "BLE_SERVICE_LOG:";


    BleDevice bleDevice;

    public BluetoothLeService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
        if (intent.getAction().equals(Constants.START_FOREGROUND_ACTION)) {
            Log.i(BLE_LOG, "Received Start Foreground Intent ");
            onStartForegroundService();

            bleDevice= BleManager.getInstance().getAllConnectedDevice().get(0);
            Log.i(BLE_LOG, "macAddr : " + bleDevice.getName() + "로 연결 시도중...");
            if(bleDevice!=null) {
                connectBle(bleDevice);

            }
        } else if (intent.getAction().equals(Constants.STOP_FOREGROUND_ACTION)) {
            Log.i(BLE_LOG, "Received Stop Foreground Intent");
            //your end servce code
            Log.i(BLE_LOG, "서비스가 중지되었습니다.");

            stopForeground(true);
            stopSelf();
        }}else {
            Log.e(BLE_LOG, "intent.getAction()이 NULL입니다.");
        }
        return START_STICKY;
    }

    private void connectBle(BleDevice bleDevice) {

        BleManager.getInstance().read(
                bleDevice,
                Constants.BLE_USER_SERVICEID3.toString(),
                Constants.BLE_GET_USER_SLEEP_DATA.toString(),
                new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        Log.i("data불러와짐.:", new String(data));
                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        Log.i("BleException.:", exception.getDescription());

                    }
                });
    }

    private void onStartForegroundService() {
        Intent stopIntent = new Intent(this, BluetoothLeService.class);
        stopIntent.setAction(Constants.STOP_FOREGROUND_ACTION);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothLeService.this, "default");
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(

                        0, "종료", stopPendingIntent
                ).build();
        builder
                .addAction(action)
                .setContentTitle("수면 케어")
                .setContentText("수면 매트와 통신 중...")

                .setSmallIcon(R.mipmap.ic_launcher);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        startForeground(1, builder.build());
    }



    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        BleManager.getInstance().disconnectAllDevice();
        super.onDestroy();
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}
