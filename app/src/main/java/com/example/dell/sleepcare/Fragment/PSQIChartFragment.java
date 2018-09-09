package com.example.dell.sleepcare.Fragment;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.Adapter.PSQIResultAdapter;
import com.example.dell.sleepcare.Model.PSQIResult;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.RESTAPI.PSQIService;
import com.example.dell.sleepcare.RESTAPI.RetrofitClient;
import com.example.dell.sleepcare.Utils.SharedPrefUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.dell.sleepcare.Utils.Constants.API_URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class PSQIChartFragment extends android.support.v4.app.Fragment implements MainActivity.OnBackPressedListener {
    Retrofit retrofit;
    PSQIService apiService;
    Call<List<PSQIResult>> psqiCall;
    List<PSQIResult> psqiList;
    SharedPreferences sp;
    HashMap<Integer, Integer> monthList;
    List<BarEntry> entries = new ArrayList<>();
    Unbinder unbinder;
    LinearLayoutManager mLayoutManager;
    PSQIResultAdapter adapter;
    @BindView(R.id.chart)
    BarChart chart;
    @BindView(R.id.recyclerview_psqi_result)
    RecyclerView psqiResultRecyclerView;


    public PSQIChartFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_psqichart, container, false);
        unbinder = ButterKnife.bind(this, view);
        mLayoutManager = new LinearLayoutManager(getContext());

        //PSQI데이터를 서버에서 읽어온다.
        getPSQIDataSet();





        return view;
    }

    //PSQI데이터를 서버에서 읽어온다.
    private void getPSQIDataSet() {
        retrofit = RetrofitClient.getClient(API_URL);
        apiService = retrofit.create(PSQIService.class);
        sp = SharedPrefUtils.getInstance(getActivity().getApplicationContext()).getPrefs();

        // ProgressDialog 초기화
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMax(100);
        progressDialog.setMessage("데이터를 받아오는 중...");
        progressDialog.setTitle("로딩");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // show it
        progressDialog.show();

        psqiCall = apiService.getPSQIList(sp.getString("email", ""));

        monthList = new HashMap<>();
        psqiCall.enqueue(new Callback<List<PSQIResult>>() {
            @Override
            public void onResponse(Call<List<PSQIResult>> call, Response<List<PSQIResult>> response) {
                progressDialog.dismiss();
                psqiList = response.body();

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                Calendar cal = Calendar.getInstance();
                for (PSQIResult s : psqiList) {
                    try {
                        cal.setTime(df.parse(s.getPSQI_DATE()));// all done
                        monthList.put(cal.get(Calendar.MONTH)+1, s.getCOMP_SUM());
                        entries.add(new BarEntry(cal.get(Calendar.MONTH)+1, s.getCOMP_SUM()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                adapter = new PSQIResultAdapter(getContext(), psqiList);
                psqiResultRecyclerView.setAdapter(adapter);
                psqiResultRecyclerView.setItemAnimator(new DefaultItemAnimator());
                initializeChart(entries);
            }

            @Override
            public void onFailure(Call<List<PSQIResult>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(), "DATA받아오기 실패...", Toast.LENGTH_LONG).show();

                t.printStackTrace();
            }
        });
    }

    //차트 초기화
    public void initializeChart(List<BarEntry> entries){
        BarDataSet dataSet = new BarDataSet(entries,"PSQI점수");
        dataSet.setColor(getResources().getColor(R.color.primaryColor));
        dataSet.setBarShadowColor(getResources().getColor(R.color.md_blue_grey_100));
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.2f);
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(entries.size());
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int)value)+"월";
            }
        });
        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int) value) + "점";
            }
        });
        chart.getDescription().setEnabled(false);
        barData.setValueTextSize(10f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMinimum(0.5f);
        chart.setTouchEnabled(false);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.setData(barData);
        chart.setFitBars(true);
        chart.animateY(750);
        chart.invalidate();
    }

    @OnClick(R.id.chart_fragment_menu)
    public void onViewClicked() {
        ((MainActivity) Objects.requireNonNull(getActivity())).mDrawer.openMenu();
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
        MainActivity activity = (MainActivity) getActivity();
        // 한번 뒤로가기 버튼을 눌렀다면 Listener 를 null 로 해제해줍니다.
        activity.setOnBackPressedListener(null);

        ((MainActivity) getActivity()).mainContentLayout.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).mainFragmentContainer.setVisibility(View.GONE);

        // Activity 에서도 뭔가 처리하고 싶은 내용이 있다면 하단 문장처럼 호출해주면 됩니다.
        // activity.onBackPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MainActivity) context).setOnBackPressedListener(this);
    }
}
