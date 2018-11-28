package com.example.dell.sleepcare.Bluetooth;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.LocalDB.DBHelper;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.Utils.Constants;
import com.example.dell.sleepcare.Utils.SharedPrefUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;

public class BluetoothLeService extends Service {

    final String BLE_LOG = "BLE_SERVICE_LOG:";
    final DBHelper dbHelper = new DBHelper(getBaseContext(), Constants.DB_NAME, null, 1);
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable(){
        @Override
        public void run(){
            getEnvData();
            mHandler.postDelayed(this, Constants.ENV_DATA_DELAY);
        }
    };



    BleDevice bleDevice;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) { // TODO Auto-generated method stub // Get extra data included in the Intent String message = intent.getStringExtra("message"); Log.d("receiver", "Got message: " + message); } };
            if (!intent.getStringExtra("method").isEmpty()) {
                switch (intent.getStringExtra("method")) {
                    case "start":
                        startOrStopMeasure(true);
                        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
                        break;
                    case "stop":
                        startOrStopMeasure(false);
                        Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_SHORT).show();
                        break;
                    case "disconnectDevice":
                        disconnectDevice();
                        Toast.makeText(getApplicationContext(), "disconnect", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Log.e(getClass().toString(), "this is not allowed >>" + intent.getStringExtra("method"));
                        break;
                }

                Log.e("BluetoothLeService", intent.getStringExtra("method"));
            }
        }
    };



    public BluetoothLeService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.EVENT_TO_BLE_SERVICE));
        String userEmail = SharedPrefUtils.getInstance(getApplicationContext()).getStringExtra("email");
        if (intent != null) {
            if (intent.getAction().equals(Constants.START_FOREGROUND_ACTION)) {
                Log.i(BLE_LOG, "Received Start Foreground Intent ");
                onStartForegroundService();

                bleDevice = BleManager.getInstance().getAllConnectedDevice().get(0);
                Log.i(BLE_LOG, "macAddr : " + bleDevice.getName() + "로 연결 시도중...");
                if (bleDevice != null) {

                    authorizeUser(bleDevice, userEmail);

                }
            } else if (intent.getAction().equals(Constants.STOP_FOREGROUND_ACTION)) {
                Log.i(BLE_LOG, "Received Stop Foreground Intent");
                //your end servce code
                Log.i(BLE_LOG, "서비스가 중지되었습니다.");

                stopForeground(true);
                stopSelf();
            }
        } else {
            Log.e(BLE_LOG, "intent.getAction()이 NULL입니다.");
        }
        return START_STICKY;
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

    private void disconnectDevice() {
        stopForeground(true);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        BleManager.getInstance().disconnectAllDevice();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }


    private void authorizeUser(BleDevice bleDevice, String userEmail) {
        if(userEmail.length()==20){
            userEmail = "#" + userEmail;
        }
        BleManager.getInstance().write(
                bleDevice,
                Constants.BLE_USER_SERVICEID1.toString(),
                Constants.BLE_SEND_USER_ID_UUID.toString(),
                userEmail.getBytes()
                , new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {

                        Toast.makeText(getApplicationContext(), "사용자 인증에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.i("BLE_RESULT_LOG :", "사용자 인증 성공, 보낸 값:  " + new String(justWrite));
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.e(BLE_LOG, exception.getCode() + exception.getDescription());

                    }
                });
    }

    public void startOrStopMeasure(boolean isStart){
        String methodNm = "";
        if(isStart){
            methodNm="startMeasure";
        } else {
            methodNm="stopMeasure";
        }
        BleManager.getInstance().write(bleDevice, Constants.BLE_USER_SERVICEID2.toString(), Constants.BLE_START_OR_STOP_SIGN_UUID.toString(), methodNm.getBytes(), new BleWriteCallback() {
            @Override
            public void onWriteSuccess(int current, int total, byte[] justWrite) {
                if(isStart) {
                    Log.i("BLE_RESULT_LOG :", "onWriteSuccess(StartOrStopMeasure) : >>" +new String(justWrite));
                    Toast.makeText(getApplicationContext(), new String(justWrite, Charset.forName("UTF-8")), Toast.LENGTH_SHORT).show();
                    mRunnable.run();
                } else {
                    stopRecordData();
                }
                Log.i(getClass().toString(), new String(justWrite, Charset.forName("UTF-8")) + "method execute");
            }

            @Override
            public void onWriteFailure(BleException exception) {
                Log.e("BLE_RESULT_LOG", exception.getDescription());
            }
        });
    }

    private void stopRecordData() {
        mHandler.removeCallbacks(mRunnable);
        getEnvDayData();
    }


    public void getEnvData() {
        Log.e(getClass().toString(), "getEnvData Executed");
        BleManager.getInstance().read(bleDevice, Constants.BLE_USER_SERVICEID3.toString(), Constants.BLE_GET_USER_ENV_DATA.toString(), new BleReadCallback() {
            @Override
            public void onReadSuccess(byte[] data) {
                Log.i("BLE_RESULT_LOG :", "onReadSuccess(getEnvData): >>>>" +new String(data));
                String dataStr = new String(data);
                dataStr = dataStr.substring(1, dataStr.length()-1);
                Map<String, String> map = jsonToMap(dataStr);
                String time = map.get("MEASURE_TIME");
                String temperature = map.get("TEMPERATURE");
                String light = map.get("LIGHT");
                String sound = map.get("SOUND");
                String humidity = map.get("HUMIDITY");
                StringBuffer sb = new StringBuffer();
                sb.append("측정시간 : ")
                        .append(time)
                        .append(" 온도: ")
                        .append(temperature)
                        .append(" 습도: ")
                        .append(humidity)
                        .append(" 소음: ")
                        .append(sound)
                        .append(" 밝기: ")
                        .append(light);
                Toast.makeText(getApplicationContext(), sb.toString()  , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReadFailure(BleException exception) {
                Log.e(getClass().toString(), "READ FAILED!!!! CHECK 31 UUID"+ exception.getDescription());
            }
        });
    }

    private void getEnvDayData() {
        BleManager.getInstance().read(bleDevice, Constants.BLE_USER_SERVICEID3.toString(), Constants.BLE_GET_USER_ENV_DAY_DATA.toString(), new BleReadCallback() {
            @Override
            public void onReadSuccess(byte[] data) {
                Log.i("BLE_RESULT_LOG :", "onReadSuccess(getEnvDayData) : >>>>" +new String(data));
                insertEnvDataToDB(new String(data));
            }

            @Override
            public void onReadFailure(BleException exception) {
                Log.i(getClass().toString(), "READ FAILED!!!! CHECK 32 UUID"+ exception.getDescription());
            }
        });
    }

    private int insertEnvDataToDB(String data){
        //data를 POJO형식의 UserEnv 로 매칭해주고, 그값을 dbHelper.insertEnv()함수에 인자로 던져준다. 결과값은 return 해준다.
        Log.i("측정중......", "측정 데이터 >>> "+ data);
        return 0;
    }

    public  Map<String, String> jsonToMap(String jsonString) {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(jsonString, type);
    }
}