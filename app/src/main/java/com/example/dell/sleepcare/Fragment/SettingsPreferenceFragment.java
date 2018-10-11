package com.example.dell.sleepcare.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.R;


public class SettingsPreferenceFragment extends PreferenceFragment {

    Preference logoutPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_pref);

        logoutPref = findPreference("logout");

        logoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent logoutIntent = new Intent(getActivity(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("action", "logout");
                logoutIntent.putExtras(bundle);
                startActivity(logoutIntent);

                return true;
            }
        });


    }
}
