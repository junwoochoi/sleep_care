package com.example.dell.sleepcare.Utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressUtils {
    public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setTitle("로딩");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        // show it
        progressDialog.show();
        return progressDialog;
    }
}
