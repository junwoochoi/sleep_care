package com.example.dell.sleepcare.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.sleepcare.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public class EnvDayFragment extends Fragment {

    Unbinder unbinder;
    public EnvDayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_env_day, container, false);
        unbinder = ButterKnife.bind(view);


        return view;
    }


}
