package com.example.dell.sleepcare.Utils;


import android.os.ParcelUuid;

import java.util.UUID;


public class Constants {
    public static final int ENV_DATA_DELAY = 6000;
    public static final String DB_NAME = "TEST_DB";
    public static final String API_URL = "http://210.102.181.158:62006/mobile/"; /* "http://192.9.20.62:62006/mobile/";*/
    public static final String NAVER_CLIENT_ID = "YzvnGn3BFQswtRux5ooh";
    public static final String NAVER_SECRET = "mULEB67iha";
    public static final String FACEBOOK_APP_ID = "1386594921477837";
    public static final String FB_LOGIN_PROTOCOL_SCHEME = "fb1386594921477837";
    public static final String PREF_USERDATA = "userData";
    public static final String GOOGLE_CLIENT_ID = "154020628759-3bnilac74b3vrfsfq2ql2s9csadco265.apps.googleusercontent.com";
    public static final String STOP_FOREGROUND_ACTION = "STOP_FOREGROUND_SERVICE";
    public static final String START_FOREGROUND_ACTION = "START_FOREGROUND_SERVICE";
    public static final  String EVENT_TO_BLE_SERVICE = "EVNET_TO_BLE_SERVICE";
    public final static UUID BLE_SEND_USER_ID_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff2");
    public final static UUID BLE_SEND_INITIALIZE_USER_ID_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-fffffffffff3");
    public final static UUID BLE_START_OR_STOP_SIGN_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff22");
    public final static UUID BLE_GET_USER_ENV_DATA = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff31");
    public final static UUID BLE_GET_USER_ENV_DAY_DATA = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff32");
    public final static UUID BLE_GET_PI_SETTING_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff41");
    public final static UUID BLE_MODIFY_PI_SETTING_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff42");
    public final static UUID BLE_SEND_REBOOT_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff43");
    public final static UUID BLE_SET_DEFAULT_UUID = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff44");
    public final static UUID BLE_USER_SERVICEID1 = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff10");
    public final static UUID BLE_USER_SERVICEID2 = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff20");
    public final static UUID BLE_USER_SERVICEID3 = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff30");
    public final static UUID BLE_USER_SERVICEID4 = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffff40");
    public final static ParcelUuid PARCEL_UUID_BLE_SERVICE =
            ParcelUuid.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFF0");
}
