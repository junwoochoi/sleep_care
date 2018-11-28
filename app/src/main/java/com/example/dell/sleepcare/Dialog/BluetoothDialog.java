package com.example.dell.sleepcare.Dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.dell.sleepcare.Bluetooth.BeaconAdapter;
import com.example.dell.sleepcare.Bluetooth.BluetoothLeService;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.Utils.Constants;
import com.example.dell.sleepcare.Utils.ProgressUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothDialog extends Dialog {

    private static final int LAYOUT = R.layout.dialog_bluetooth_list;
    private final String BLE_SCAN_LOG = "BLE_SCAN:";

    @BindView(R.id.beaconListView)
    ListView beaconListView;
    @BindView(R.id.btn_bt_dialog_cancel)
    MaterialButton cancelBtn;
    @BindView(R.id.btn_bt_dialog_connect)
    MaterialButton connectBtn;


    private Context context;
    private ArrayList<BleDevice> beacons;
    private BeaconAdapter beaconAdapter;
    private BleDevice selectedDevice;
    private ProgressDialog progressDialog;


    public BluetoothDialog(@NonNull Context context) {
        super(context);
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
        beacons = new ArrayList<>();


        scanLeDevice();


        //리스트뷰 초기화
        beaconAdapter = new BeaconAdapter(beacons, getLayoutInflater());
        beaconListView.setAdapter(beaconAdapter);
        beaconListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        beaconListView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (selectedDevice != null) {
                selectedDevice = null;
            }
            selectedDevice = beacons.get(i);
            Toast.makeText(context, selectedDevice.getName()+"선택되었습니다.", Toast.LENGTH_LONG).show();
        });

    }

    @OnClick(R.id.btn_bt_dialog_connect)
    void onConnect() {
        Intent intent = new Intent(context.getApplicationContext(), BluetoothLeService.class);
        BleManager.getInstance().connect(selectedDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.i(BLE_SCAN_LOG, "onStartConnect");
                progressDialog = ProgressUtils.showProgressDialog(getContext(), "블루투스 연결중...");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                if(progressDialog!=null){
                    progressDialog.dismiss();
                }
                Log.e(BLE_SCAN_LOG, "onConnectFail"+ exception.getDescription());

            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                if(progressDialog!=null){
                    progressDialog.dismiss();
                }
                intent.putExtra("bleDevice", selectedDevice);
                Log.e("click:", "onClick");
                intent.setAction(Constants.START_FOREGROUND_ACTION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent);
                }
                context.startService(intent);
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "수면 매트에 연결되었습니다. ", Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorSecondary));
                snackbar.show();
                Toast.makeText(context, "연결 및 사용자 인증 완료",  Toast.LENGTH_SHORT).show();
                dismiss();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.i(BLE_SCAN_LOG, "onDisConnected");

            }
        });

    }

    @OnClick(R.id.btn_bt_dialog_cancel)
    void onCancel() {
        BleManager.getInstance().cancelScan();
        cancel();
    }




    private void scanLeDevice() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceName(false, "test")
                .setAutoConnect(false)
                .setScanTimeOut(5000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if (success) {
                    Log.i(BLE_SCAN_LOG, "BLE_MANAGER onScanStarted....");
                } else {
                    Log.e(BLE_SCAN_LOG, "BLE_MANAGER onScanStarted FAIL!!");
                }
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.i(BLE_SCAN_LOG, bleDevice.getName()+"found");

                beacons.add(bleDevice);
                beaconAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

                Toast.makeText(context, "스캔이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }
}
