package com.example.dell.sleepcare.Fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dell.sleepcare.Adapter.TestResultAdapter;
import com.example.dell.sleepcare.Model.PSQIScore;
import com.example.dell.sleepcare.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class TestResultFragment extends DialogFragment {
    PSQIScore psqiScore;
    @BindView(R.id.button_cancel)
    ImageView buttonCancel;
    @BindView(R.id.recyclerview_result)
    RecyclerView recyclerviewResult;
    Unbinder unbinder;

    GridLayoutManager mGridLayoutManager;
    TestResultAdapter adapter;
    ArrayList<Integer> list;

    public TestResultFragment() {
        // Required empty public constructor
    }

    public static TestResultFragment newInstance(PSQIScore score) {
        TestResultFragment fragment = new TestResultFragment();
        fragment.psqiScore = score;


        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_fullscreen);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_result, container, false);
        unbinder = ButterKnife.bind(this, view);
        // Inflate the layout for this fragment

        mGridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerviewResult.setLayoutManager(mGridLayoutManager);

        list = new ArrayList<Integer>();
        list.add(psqiScore.getComponent1());
        list.add(psqiScore.getComponent2());
        list.add(psqiScore.getComponent3());
        list.add(psqiScore.getComponent4());
        list.add(psqiScore.getComponent5());
        list.add(psqiScore.getComponent6());
        list.add(psqiScore.getComponent7());
        list.add(psqiScore.getComponent());

        adapter = new TestResultAdapter(getContext(), list);
        recyclerviewResult.setAdapter(adapter);
        recyclerviewResult.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //다이얼로그 풀스크린으로 켜기
        Dialog dialog = getDialog();

        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.button_cancel)
    public void onViewClicked() {
        this.dismiss();
    }
}
