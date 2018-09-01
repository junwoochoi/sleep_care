package com.example.dell.sleepcare.Utils;


import android.os.ParcelUuid;

import java.util.UUID;


public class Constants {

    public static final String API_URL = "http://210.102.181.158:62006/mobile/"; /* "http://192.9.20.62:62006/mobile/";*/
    public static final String NAVER_CLIENT_ID = "YzvnGn3BFQswtRux5ooh";
    public static final String NAVER_SECRET = "mULEB67iha";
    public static final String FACEBOOK_APP_ID = "1386594921477837";
    public static final String FB_LOGIN_PROTOCOL_SCHEME = "fb1386594921477837";
    public static final String PREF_USERDATA = "userData";
    public static final String GOOGLE_CLIENT_ID = "154020628759-3bnilac74b3vrfsfq2ql2s9csadco265.apps.googleusercontent.com";

    public final static UUID BLE_NOTIFY_1_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff1");
    public final static UUID BLE_NOTIFY_2_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff2");
    public final static UUID BLE_NOTIFY_3_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff3");
    public final static UUID BLE_NOTIFY_4_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff4");
    public final static UUID BLE_NOTIFY_6_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff6");

    public final static UUID BLE_SERVICE_SAMPLE_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff0");
    public final static ParcelUuid PARCEL_UUID_BLE_SERVICE =
            ParcelUuid.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFF0");
}
