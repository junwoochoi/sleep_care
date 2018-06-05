package com.example.dell.sleepcare.Fragment;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;
import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.Model.PSQIResult;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.RESTAPI.PSQIService;
import com.example.dell.sleepcare.Utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class PSQIChartFragment extends android.support.v4.app.Fragment implements MainActivity.OnBackPressedListener {
    Retrofit retrofit;
    PSQIService apiService;
    Call<List<PSQIResult>> psqiCall;
    List<PSQIResult> psqiList;
    SharedPreferences sp;
    @BindView(R.id.chart)
    LineChartView chart;
    Unbinder unbinder;


    public PSQIChartFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_psqichart, container, false);
        unbinder = ButterKnife.bind(this, view);

        //PSQI데이터를 서버에서 읽어온다.
        getPSQIDataSet();

        String[] labels = { "Jan", "Feb", "Mar", "Apr", "May", "Jul", "Aug", "Sep" };
        float[] values = { 3.0f, 4.0f, 1.0f, 3.3f, 6.6f, 3.0f, 4.0f, 5.0f };
        LineSet dataset = new LineSet(labels, values);
        dataset.setColor(Color.parseColor("#b3b5bb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#ffc755"))
                .setThickness(4)
                .setSmooth(true)
                .endAt(8);
        chart.addData(dataset);
        chart.setAxisBorderValues(0, 20)
                .setYLabels(AxisRenderer.LabelPosition.NONE)
                .show(new Animation().setInterpolator(new BounceInterpolator())
                        .fromAlpha(0));

                chart.show();

        return view;
    }

    //PSQI데이터를 서버에서 읽어온다.
    private void getPSQIDataSet() {
        retrofit = new Retrofit.Builder().baseUrl(Constants.API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        apiService = retrofit.create(PSQIService.class);
        sp = getActivity().getSharedPreferences("userData", Context.MODE_PRIVATE);


        psqiCall = apiService.getPSQIList(sp.getString("email", ""));

        psqiCall.enqueue(new Callback<List<PSQIResult>>() {
            @Override
            public void onResponse(Call<List<PSQIResult>> call, Response<List<PSQIResult>> response) {
                psqiList = response.body();
            }

            @Override
            public void onFailure(Call<List<PSQIResult>> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), "DATA받아오기 실패...", Toast.LENGTH_LONG).show();
                ;
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onBack() {
        Log.e("Other", "onBack()");
        // 리스너를 설정하기 위해 Activity 를 받아옵니다.
        MainActivity activity = (MainActivity)getActivity();
        // 한번 뒤로가기 버튼을 눌렀다면 Listener 를 null 로 해제해줍니다.
        activity.setOnBackPressedListener(null);

        ((MainActivity) getActivity()).mainContentLayout.setVisibility(View.VISIBLE);
        // Activity 에서도 뭔가 처리하고 싶은 내용이 있다면 하단 문장처럼 호출해주면 됩니다.
        // activity.onBackPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity)context).setOnBackPressedListener(this);
    }
}
