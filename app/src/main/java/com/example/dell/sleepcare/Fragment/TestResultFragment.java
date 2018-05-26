package com.example.dell.sleepcare.Fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dell.sleepcare.Model.PSQIScore;
import com.example.dell.sleepcare.R;


public class TestResultFragment extends DialogFragment {
    PSQIScore psqiScore;

    public TestResultFragment() {
        // Required empty public constructor
    }

    public static TestResultFragment newInstance(PSQIScore score) {
        TestResultFragment fragment = new TestResultFragment();
        fragment.psqiScore=score;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_result, container, false);
    }

}
