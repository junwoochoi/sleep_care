package com.example.dell.sleepcare.Bluetooth;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dell.sleepcare.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothDialog extends Dialog {

    private static final int LAYOUT = R.layout.dialog_bluetooth_list;
    private static final long SCAN_PERIOD = 10000;

    @BindView(R.id.beaconListView)
    ListView beaconListView;
    @BindView(R.id.btn_bt_dialog_cancel)
    MaterialButton cancelBtn;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private Context context;
    private Activity activity;
    ArrayList<BluetoothDevice> beacon;
    BeaconAdapter beaconAdapter;
    BluetoothLeScanner bluetoothLeScanner;


    public BluetoothDialog(@NonNull Context context, Activity activity) {
        super(context);
        this.activity = activity;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(LAYOUT);
        setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);

        //블루투스 관련 초기화
        beacon = new ArrayList<>();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler();
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        scanLeDevice(true);

        //리스트뷰 클릭
        beaconListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               if(view.isSelected()){
                   view.setSelected(false);
               } else {
                   view.setSelected(true);
               }

            }
        });

        //취소버튼 onClick
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothLeScanner.stopScan(mLeScanCallback);
                cancel();
            }
        });



    }

    private void scanLeDevice(final boolean enable) {


        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;

                    bluetoothLeScanner.stopScan(mLeScanCallback);
                    if(beacon.isEmpty()) {
                        Toast.makeText(context, "디바이스를 찾을 수 없습니다. \n 디바이스 근처에서 재시도 해주세요", Toast.LENGTH_LONG).show();
                        cancel();
                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(mLeScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(mLeScanCallback);
        }
    }
    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.e("BT log : ", "블루투스 스캔 성공..."+result.toString());
            try {
                final ScanRecord scanRecord = result.getScanRecord();

                Log.d("getTxPowerLevel()",scanRecord.getTxPowerLevel()+"");
                Log.d("onScanResult()", result.getDevice().getAddress() + "\n" + result.getRssi() + "\n" + result.getDevice().getName()
                        + "\n" + result.getDevice().getBondState() + "\n" + result.getDevice().getType());

                final ScanResult scanResult = result;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(activity == null)
                            activity = (Activity) context;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!beacon.contains(scanResult.getDevice())) {
                                    beacon.add(scanResult.getDevice());
                                }
                                beaconAdapter = new BeaconAdapter(beacon, getLayoutInflater());
                                beaconListView.setAdapter(beaconAdapter);
                                beaconAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e("BT log : ", "블루투스 스캔 실패..." + String.valueOf(errorCode));
        }
    };
}
