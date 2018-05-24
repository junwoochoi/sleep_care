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
import android.widget.Toast;

import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.Utils.Constants;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.StringTokenizer;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class BluetoothLeService extends Service {

    RxBleClient  rxBleClient;
    String macAddr;
    RxBleDevice bleDevice;
    Disposable connectionDisposable;
    public BluetoothLeService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStartForegroundService();
        rxBleClient = RxBleClient.create(this);
        macAddr = Objects.requireNonNull(intent.getExtras()).getString("macAddr");
        Log.i("bluetooth 서비스 실행", "macAddr : "+macAddr+"로 연결 시도중...");
        subscribeNotification();
        return START_STICKY;
    }

    private void onStartForegroundService() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(BluetoothLeService.this, "default");
        builder
                .setContentTitle("수면 케어")
                .setContentText("수면 매트와 연결 중...")
                .setSmallIcon(R.mipmap.ic_launcher);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel( new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        startForeground(1, builder.build());
    }


    public void subscribeNotification(){
        if(macAddr != null) {
            bleDevice = rxBleClient.getBleDevice(macAddr);
            Log.d("getBLEDEVICE", rxBleClient.getBleDevice(macAddr).getName());
            if (isConnected()) {
                connectionDisposable = null;
            }
            connectionDisposable = bleDevice.establishConnection(false)
                    .flatMap(rxBleConnection -> rxBleConnection.setupNotification(Constants.BLE_NOTIFY_6_UUID))
                    .flatMap(notificationObservable -> notificationObservable)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onNotificationReceived, Throwable::printStackTrace);
        }
    }

    private void onNotificationReceived(byte[] bytes) {
        try {
            String result = new String(bytes, "UTF-8");
            StringTokenizer tokenizer = new StringTokenizer(result,",");

            Log.d("notificationReceived: ", "온도: "+tokenizer.nextToken()+" 습도: "+tokenizer.nextToken()+" 빛: "+tokenizer.nextToken()+" 소음: "+tokenizer.nextToken());
        } catch (UnsupportedEncodingException e) {
            Log.e("getCause값: ", e.getCause().toString());
            Toast.makeText(this, "블루투스 연결이 끊겼습니다.", Toast.LENGTH_LONG).show();
            stopForeground(true);
            stopSelf();
            e.printStackTrace();

        }
    }

    private boolean isConnected() {
        if(connectionDisposable != null){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public void close() {
        if (rxBleClient == null) {
            return;
        }
        rxBleClient = null;
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
}
