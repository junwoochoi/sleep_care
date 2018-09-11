package com.example.dell.sleepcare.Activitity;

import android.Manifest;
import android.app.ActivityManager;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dell.sleepcare.Bluetooth.BluetoothLeService;
import com.example.dell.sleepcare.Dialog.BluetoothDialog;
import com.example.dell.sleepcare.Fragment.MenuListFragment;
import com.example.dell.sleepcare.Fragment.PSQIChartFragment;
import com.example.dell.sleepcare.Fragment.TestFragment;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.RESTAPI.LoginResult;
import com.example.dell.sleepcare.RESTAPI.LoginService;
import com.example.dell.sleepcare.RESTAPI.RetrofitClient;
import com.example.dell.sleepcare.Utils.Constants;
import com.example.dell.sleepcare.Utils.SharedPrefUtils;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.dell.sleepcare.Utils.Constants.API_URL;
import static com.example.dell.sleepcare.Utils.Constants.GOOGLE_CLIENT_ID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    @BindView(R.id.main_content)
    public RelativeLayout mainContentLayout;
    @BindView(R.id.main_container)
    public FrameLayout mainFragmentContainer;
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
    Retrofit retrofit;
    LoginService loginService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setLoginSetting();
        sp = SharedPrefUtils.getInstance(this).getPrefs();
        editor = sp.edit();

        checkLogin();

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

    private void checkLogin() {
        retrofit = RetrofitClient.getClient(API_URL);
        loginService = retrofit.create(LoginService.class);
        Call<LoginResult> rs = loginService.login(sp.getString("email", ""));
        rs.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    LoginResult res = response.body();
                    if (res != null) {
                        if (!res.getLoginResult().equals("200")) {
                            Toast.makeText(getApplicationContext(), "데이터 불러오기 실패!", Toast.LENGTH_LONG).show();
                            editor.clear();
                            editor.apply();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "데이터 불러오기 실패!", Toast.LENGTH_LONG).show();
                editor.clear();
                editor.apply();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
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
        mainFragmentContainer.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.test_result_btn)
    void onGraphClicked(){
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment psqiChartFragment = new PSQIChartFragment();
        fm.beginTransaction().replace(R.id.main_container, psqiChartFragment).commit();
        mainContentLayout.setVisibility(View.GONE);
        mainFragmentContainer.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawer.openMenu();
                break;
            }
            case R.id.action_bt1: {
                Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(intent);
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
        public void updateUI(){
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
                                        Intent stopIntent = new Intent(MainActivity.this, BluetoothLeService.class);
                                        stopIntent.setAction(Constants.STOP_FOREGROUND_ACTION);
                                        startService(stopIntent);
                                        btnBluetooth.setText("연결하기");
                                        Toast.makeText(getApplicationContext(), "서비스가 종료되었습니다.", Toast.LENGTH_SHORT).show();
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

    // 뒤로가기 버튼 입력시간이 담길 long 객체
    private long pressedTime = 0;

    // 리스너 생성
    public interface OnBackPressedListener {
        public void onBack();
    }

    // 리스너 객체 생성
    private OnBackPressedListener mBackListener;

    // 리스너 설정 메소드
    public void setOnBackPressedListener(OnBackPressedListener listener) {
        mBackListener = listener;
    }

    // 뒤로가기 버튼을 눌렀을 때의 오버라이드 메소드
    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
        // 다른 Fragment 에서 리스너를 설정했을 때 처리됩니다.
        if(mBackListener != null) {
            mBackListener.onBack();
            Log.e("!!!", "Listener is not null");
            // 리스너가 설정되지 않은 상태(예를들어 메인Fragment)라면
            // 뒤로가기 버튼을 연속적으로 두번 눌렀을 때 앱이 종료됩니다.
        } else {
            Log.e("!!!", "Listener is null");
            if ( pressedTime == 0 ) {
                Snackbar.make(findViewById(R.id.main_container),
                        " 한 번 더 누르면 종료됩니다." , Snackbar.LENGTH_LONG).show();
                pressedTime = System.currentTimeMillis();
            }
            else {
                int seconds = (int) (System.currentTimeMillis() - pressedTime);

                if ( seconds > 2000 ) {
                    Snackbar.make(findViewById(R.id.main_container),
                            " 한 번 더 누르면 종료됩니다." , Snackbar.LENGTH_LONG).show();
                    pressedTime = 0 ;
                }
                else {
                    super.onBackPressed();
                    Log.e("!!!", "onBackPressed : finish, killProcess");
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
            }
        }
    }
    }


