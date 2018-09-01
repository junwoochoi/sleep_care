package com.example.dell.sleepcare.Activitity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.Utils.SharedPrefUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler hd = new Handler();
        //상단바와 하단네비바 색상 지정
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark)); //status bar or the time bar at the top
        }
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = SharedPrefUtils.getInstance(getApplicationContext()).getPrefs();
                SharedPreferences.Editor edit = sp.edit();
                if(sp.getString("email","").length()>0){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        }, 1600); // 3초 후 이미지를 닫습니다

    }
}
