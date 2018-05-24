package com.example.dell.sleepcare.Activitity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dell.sleepcare.Bluetooth.BluetoothDialog;
import com.example.dell.sleepcare.Bluetooth.BluetoothLeService;
import com.example.dell.sleepcare.Fragment.MenuListFragment;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.Fragment.TestFragment;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.nhn.android.naverlogin.OAuthLogin;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.dell.sleepcare.Utils.Constants.GOOGLE_CLIENT_ID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    @BindView(R.id.main_content)
    RelativeLayout mainContentLayout;
    @BindView(R.id.my_toolbar)
    Toolbar myToolbar;
    @BindView(R.id.drawerlayout) public FlowingDrawer mDrawer;
    @BindView(R.id.button_bluetooth_on_off)
    MaterialButton btnBluetooth;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    OAuthLogin mOAuthLoginModule = OAuthLogin.getInstance();
    GoogleApiClient mGoogleApiClient;
    BluetoothDialog bluetoothDialog;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setLoginSetting();
        sp = this.getSharedPreferences("userData", MODE_PRIVATE);
        editor = sp.edit();


        //권한 요청 ( Location )
        requestPermission();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth를 지원하지 않아 이용할 수 없습니다. 죄송합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        //상단바와 하단네비바 색상 지정
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); //status bar or the time bar at the top
        }

        //툴바 초기화

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        // FlowingDrawer 초기화
        setupMenu();
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                    Log.i("MainActivity", "Drawer STATE_CLOSED");
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
            }
        });

        //블루투스 켜지도록 요청
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothDialog = new BluetoothDialog(MainActivity.this);
                bluetoothDialog.show();
            }
        });
    }



    public void setLoginSetting() {
        //로그인 세팅 초기화
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder
                (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.e("hello", "google login failed!!!");
                            }
                        } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }
    @OnClick(R.id.psqi_test_btn)
    void onTestClicked(){
        FragmentManager fm = getSupportFragmentManager();
        TestFragment testFragment = new TestFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, testFragment).commit();
        mainContentLayout.setVisibility(View.GONE);
    }

    //뒤로가기 동작 제어
    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawer.openMenu();
                break;
            }
            case R.id.action_bt1: {
                Toast.makeText(getApplicationContext(), "설정버튼 클릭!", Toast.LENGTH_LONG).show();
                onLogOut();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void setupMenu() {
        FragmentManager fm = getSupportFragmentManager();
        MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = MenuListFragment.newInstance();
            fm.beginTransaction().add(R.id.container_menu, mMenuFragment).commit();
        }
    }

    private void onLogOut() {
        mOAuthLoginModule.logout(getApplicationContext());
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        editor.clear();
                        editor.apply();
                        Log.d("SharedPreference:", sp.getAll().values().toString());
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });
        editor.clear();
        editor.apply();
        Log.d("SharedPreference:", sp.getAll().values().toString());
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        updateUI();
        super.onResume();
    }

    @Override
    protected void onPause() {
        updateUI();
        super.onPause();
        if(bluetoothDialog!=null){
        bluetoothDialog.dismiss();
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //REQUEST FOR PERMISSSION
        public void requestPermission() {
            if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.M) {
                int permissionResult = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                if (permissionResult == PackageManager.PERMISSION_DENIED) {
                    /*
                     * 거부한 이력이 한번이라도 있다면, true를 리턴한다.
                     */
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle("권한이 필요합니다.")
                                .setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\" 권한이 필요합니다. 계속하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                                        }
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MainActivity.this, "기능을 취소했습니다. 블루투스 기능을 이용할 수 없습니다.\n 이용을 원하실 시 설정에서 권한을 재설정해주십시오.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .create().show();
                    }
                    //최초로 권한을 요청할 때
                    else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
                    }
                }
            }
        }
        private void updateUI(){
            Log.e("isServiceRunning?", String.valueOf(isMyServiceRunning(BluetoothLeService.class)));
            if (isMyServiceRunning(BluetoothLeService.class)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnBluetooth.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent stopIntent = new Intent(MainActivity.this, Service.class);
                                        stopService(stopIntent);
                                    }
                                });
                                btnBluetooth.setText("연결취소");
                            }
                        });
                    }
                }).start();
            } else {
                btnBluetooth.setClickable(true);
                btnBluetooth.setText("연결하기");
            }
        }
    }


