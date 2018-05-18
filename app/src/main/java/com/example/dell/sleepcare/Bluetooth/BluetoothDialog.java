package com.example.dell.sleepcare.Bluetooth;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dell.sleepcare.R;
import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.RxBleDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

import static com.example.dell.sleepcare.Utils.Constants.UUID_BLE_SERVICE;

public class BluetoothDialog extends Dialog {

    private static final int LAYOUT = R.layout.dialog_bluetooth_list;
    private static final long SCAN_PERIOD = 10000;

    @BindView(R.id.beaconListView)
    ListView beaconListView;
    @BindView(R.id.btn_bt_dialog_cancel)
    MaterialButton cancelBtn;
    @BindView(R.id.btn_bt_dialog_connect)
    MaterialButton connectBtn;
    @BindView(R.id.btn_bt_dialog_read)
    MaterialButton readBtn;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private Context context;
    private Activity activity;
    private ArrayList<BluetoothDevice> beacon;
    private BeaconAdapter beaconAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private String macAddr;
    BluetoothConnector bluetoothConnector;
    RxBleClient rxBleClient;
    RxBleDevice device;



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
        rxBleClient = RxBleClient.create(context);
        //블루투스 관련 초기화
        beacon = new ArrayList<>();

        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        assert bluetoothManager != null;
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler();
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        scanLeDevice(true);

        //리스트뷰 초기화
        beaconAdapter = new BeaconAdapter(beacon, getLayoutInflater());
        beaconListView.setAdapter(beaconAdapter);
        beaconListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        beaconListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(macAddr!=null){
                    macAddr = null;
                }
                macAddr = beacon.get(i).getAddress();
                Toast.makeText(context, macAddr, Toast.LENGTH_LONG).show();
            }
        });

        //연결버튼 onClick
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(macAddr != null) {
                    Log.e("selectedItem : ", macAddr);
                    Toast.makeText(context, macAddr+"에 연결합니다....", Toast.LENGTH_LONG).show();
                    //bluetoothConnector = BluetoothConnector.getInstance(context);
                    //bluetoothConnector.connectBluetooth(macAddr);
                    device = rxBleClient.getBleDevice(macAddr);
                    Log.d("getBLEDEVICE", rxBleClient.getBleDevice(macAddr).getName());
                    Disposable disposable = device.establishConnection(false) // <-- autoConnect flag
                            .subscribe(
                                    rxBleConnection -> {
                                        // All GATT operations are done through the rxBleConnection.
                                        Log.d("BLE CONNECTION :", "SUCCESS!!" + device.getName());
                                    },
                                    throwable -> {
                                        // Handle an error here.
                                        Log.e("BLE CONNECTION: " , "ERROR CANNOT CONNECT TO " + macAddr);
                                    }
                            );
                    disposable.dispose();
                }
            }
        });

        readBtn.setOnClickListener(view -> {

        });
        //취소버튼 onClick
        cancelBtn.setOnClickListener(view -> {
            bluetoothLeScanner.stopScan(mLeScanCallback);
            cancel();
        });



    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(() -> {
                mScanning = false;

                bluetoothLeScanner.stopScan(mLeScanCallback);
                if(beacon.isEmpty()) {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "디바이스를 찾을 수 없습니다. \n 디바이스 근처에서 재시도 해주세요", Snackbar.LENGTH_LONG).show();
                    cancel();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
            bluetoothLeScanner.startScan(scanFilters(UUID_BLE_SERVICE), settings, mLeScanCallback);
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

    private List<ScanFilter> scanFilters(ParcelUuid serviceUUIDs) {
        List<ScanFilter> list = new ArrayList<>();

            ScanFilter filter = new ScanFilter.Builder().setServiceUuid(serviceUUIDs).build();
            list.add(filter);

        return list;
    }


}
