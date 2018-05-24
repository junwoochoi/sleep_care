package com.example.dell.sleepcare.Bluetooth;

import android.app.Dialog;
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

import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.Utils.Constants;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;
import com.polidea.rxandroidble2.exceptions.BleScanException;
import com.polidea.rxandroidble2.scan.ScanFilter;
import com.polidea.rxandroidble2.scan.ScanSettings;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class BluetoothDialog extends Dialog {

    private static final int LAYOUT = R.layout.dialog_bluetooth_list;
    private static final long SCAN_PERIOD = 10000;

    @BindView(R.id.beaconListView)
    ListView beaconListView;
    @BindView(R.id.btn_bt_dialog_cancel)
    MaterialButton cancelBtn;
    @BindView(R.id.btn_bt_dialog_connect)
    MaterialButton connectBtn;


    private Context context;
    private ArrayList<RxBleDevice> beacon;
    private BeaconAdapter beaconAdapter;
    private String macAddr;
    RxBleClient rxBleClient;
    RxBleDevice bleDevice;
    Disposable scanDisposable, connectionDisposable;


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
        rxBleClient = RxBleClient.create(context);
        //블루투스 관련 초기화
        beacon = new ArrayList<>();


        scanLeDevice(true);


        //리스트뷰 초기화
        beaconAdapter = new BeaconAdapter(beacon, getLayoutInflater());
        beaconListView.setAdapter(beaconAdapter);
        beaconListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        beaconListView.setOnItemClickListener((adapterView, view, i, l) -> {
            if (macAddr != null) {
                macAddr = null;
            }
            macAddr = beacon.get(i).getMacAddress();
            Toast.makeText(context, macAddr, Toast.LENGTH_LONG).show();
        });

    }
    @OnClick(R.id.btn_bt_dialog_connect)
    void onConnect(){
        scanLeDevice(false);
        Intent intent = new Intent(context.getApplicationContext(), BluetoothLeService.class);
        intent.putExtra("macAddr", macAddr);
        Log.e("click:", "onClick");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }
        context.startService(intent);
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),  "수면 매트에 연결되었습니다. ", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.colorSecondary));
        snackbar.show();
        dismiss();
    }

    @OnClick(R.id.btn_bt_dialog_cancel)
    void onCancel(){
        if(scanDisposable!=null) {
            scanDisposable.dispose();
        }
        cancel();
    }

    private void disposeScan() {
        if(scanDisposable!=null) {
            scanDisposable = null;
        }
        beacon.clear();
    }


    private void scanLeDevice(final boolean enable) {
        if(enable){
        if (isScanning()) {
            scanDisposable.dispose();
        } else {
            scanDisposable = rxBleClient.scanBleDevices(
                    new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                            .build(),
                    new ScanFilter.Builder()
                            .setServiceUuid(Constants.PARCEL_UUID_BLE_SERVICE)
                            .build()
            )
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(this::disposeScan)
                    .subscribe(scanResult -> {
                        if(!beacon.contains(scanResult.getBleDevice())){
                            beacon.add(scanResult.getBleDevice());
                        }
                            beaconAdapter.notifyDataSetChanged();
                    }, this::onScanFailure);
        }}
        else {
            disposeScan();
        }

    }

    private void onScanFailure(Throwable throwable) {
        if (throwable instanceof BleScanException) {
            handleBleScanException((BleScanException) throwable);
        }
    }

    private void handleBleScanException(BleScanException bleScanException) {
        final String text;

        switch (bleScanException.getReason()) {
            case BleScanException.BLUETOOTH_NOT_AVAILABLE:
                text = "Bluetooth is not available";
                break;
            case BleScanException.BLUETOOTH_DISABLED:
                text = "Enable bluetooth and try again";
                break;
            case BleScanException.LOCATION_PERMISSION_MISSING:
                text = "On Android 6.0 location permission is required. Implement Runtime Permissions";
                break;
            case BleScanException.LOCATION_SERVICES_DISABLED:
                text = "Location services needs to be enabled on Android 6.0";
                break;
            case BleScanException.SCAN_FAILED_ALREADY_STARTED:
                text = "Scan with the same filters is already started";
                break;
            case BleScanException.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                text = "Failed to register application for bluetooth scan";
                break;
            case BleScanException.SCAN_FAILED_FEATURE_UNSUPPORTED:
                text = "Scan with specified parameters is not supported";
                break;
            case BleScanException.SCAN_FAILED_INTERNAL_ERROR:
                text = "Scan failed due to internal error";
                break;
            case BleScanException.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES:
                text = "Scan cannot start due to limited hardware resources";
                break;
            case BleScanException.UNDOCUMENTED_SCAN_THROTTLE:
                text = String.format(
                        Locale.getDefault(),
                        "Android 7+ does not allow more scans. Try in %d seconds",
                        secondsTill(bleScanException.getRetryDateSuggestion())
                );
                break;
            case BleScanException.UNKNOWN_ERROR_CODE:
            case BleScanException.BLUETOOTH_CANNOT_START:
            default:
                text = "Unable to start scanning";
                break;
        }
        Log.w("EXCEPTION", text, bleScanException);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private Object secondsTill(Date retryDateSuggestion) {
        return TimeUnit.MILLISECONDS.toSeconds(retryDateSuggestion.getTime() - System.currentTimeMillis());
    }

    private boolean isScanning() {
        return scanDisposable != null;
    }


}
