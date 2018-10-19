package com.example.dell.sleepcare.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.Utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RemoteDialog extends Dialog {

    @BindView(R.id.send_start_sign)
    TextView sendStartSign;
    @BindView(R.id.send_stop_sign)
    TextView sendStopSign;
    @BindView(R.id.send_disconnect_sign)
    TextView sendDisconnectSign;
    @BindView(R.id.send_blabla_sign)
    TextView sendBlablaSign;

    public RemoteDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_remote);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.send_start_sign, R.id.send_stop_sign, R.id.send_disconnect_sign, R.id.send_blabla_sign})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.send_start_sign:
                Log.d("messageService", "Broadcasting message");
                Intent startIntent = new Intent(Constants.EVENT_TO_BLE_SERVICE);
                startIntent.putExtra("method", "start");
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(startIntent);
                break;
            case R.id.send_stop_sign:
                Log.d("messageService", "Broadcasting message");
                Intent stopIntent = new Intent(Constants.EVENT_TO_BLE_SERVICE);
                stopIntent.putExtra("method", "stop");
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(stopIntent);
                break;
            case R.id.send_disconnect_sign:
                Intent disconnectIntent = new Intent(Constants.EVENT_TO_BLE_SERVICE);
                disconnectIntent.putExtra("method", "disconnectDevice");
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(disconnectIntent);
                break;
            case R.id.send_blabla_sign:
                break;

        }
    }
}
