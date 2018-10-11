package com.example.dell.sleepcare.Fragment;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.sleepcare.Adapter.EnvMarkerView;
import com.example.dell.sleepcare.Model.EnvResult;
import com.example.dell.sleepcare.Model.UserEnv;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.RESTAPI.EnvService;
import com.example.dell.sleepcare.RESTAPI.RetrofitClient;
import com.example.dell.sleepcare.Utils.Constants;
import com.example.dell.sleepcare.Utils.ProgressUtils;
import com.example.dell.sleepcare.Utils.SharedPrefUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class EnvFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    Unbinder unbinder;
    FloatingActionButton calendarFbBtn;
    String email;
    Retrofit retrofit;
    EnvService envService;
    @BindView(R.id.env_humid_chart)
    LineChart envHumidChart;
    @BindView(R.id.env_temp_chart)
    LineChart envTempChart;
    @BindView(R.id.env_loud_chart)
    LineChart envLoudChart;
    @BindView(R.id.env_light_chart)
    LineChart envLightChart;

    List<LineChart> chartList;

    public EnvFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_env, container, false);
        calendarFbBtn = (FloatingActionButton) view.findViewById(R.id.calendar_fb_btn);
        calendarFbBtn.setOnClickListener(this);
        unbinder = ButterKnife.bind(this, view);
        email = SharedPrefUtils.getInstance(getContext()).getStringExtra("email");
        retrofit = RetrofitClient.getClient(Constants.API_URL);
        envService = retrofit.create(EnvService.class);

        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        chartList = new ArrayList<>();
        chartList.add(envHumidChart);
        chartList.add(envTempChart);
        chartList.add(envLightChart);
        chartList.add(envLoudChart);

        for (LineChart chart : chartList) {
            initializeChart(chart);
        }

        renderDefaultChart(email, envService, yesterday);

        return view;
    }


    private void renderDefaultChart(String email, EnvService envService, Calendar yesterday) {

        //로딩바
        ProgressDialog progressDialog = ProgressUtils.showProgressDialog(getContext(), "데이터 불러오는 중...");
        int year = yesterday.get(Calendar.YEAR);
        int month = yesterday.get(Calendar.MONTH) + 1;
        int dateOfMonth = yesterday.get(Calendar.DATE);
        String monthStr = String.valueOf(month);
        String dateOfMonthStr = String.valueOf(dateOfMonth);
        if (month < 10) {
            monthStr = "0" + monthStr;
        }
        if (dateOfMonth < 10) {
            dateOfMonthStr = "0" + dateOfMonthStr;
        }

        String yesterdayStr = yesterday.get(Calendar.YEAR) + "-"
                + monthStr + "-"
                + dateOfMonthStr;
        Call<EnvResult> resultCall = envService.getEnvList(email, yesterdayStr);
        resultCall.enqueue(new Callback<EnvResult>() {
            @Override
            public void onResponse(Call<EnvResult> call, Response<EnvResult> response) {
                if (response.isSuccessful()) {
                    EnvResult body = response.body();
                    Log.i(getClass().toString(), "response Success!! >>");
                    Log.i(getClass().toString(), new Gson().toJson(response.body()));


                    if (response.body().getUserEnv().size() > 0) {
                        List<UserEnv> userEnvList = response.body().getUserEnv();
                        List<Entry> tempEntries = new ArrayList<>();
                        List<Entry> humidEntries = new ArrayList<>();
                        List<Entry> loudEntries = new ArrayList<>();
                        List<Entry> lightEntries = new ArrayList<>();
                        for (UserEnv userEnv : userEnvList) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                            Date date = null;
                            try {
                                date = sdf.parse(userEnv.getEnvTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            int timeInt = cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE);
                            float timeFloat = (float) timeInt;
                            humidEntries.add(new Entry(timeFloat, Float.parseFloat(userEnv.getEnvHumid())));
                            tempEntries.add(new Entry(timeFloat, Float.parseFloat(userEnv.getEnvTemp())));
                            loudEntries.add(new Entry(timeFloat, Float.parseFloat(userEnv.getEnvLoud())));
                            lightEntries.add(new Entry(timeFloat, Float.parseFloat(userEnv.getEnvLight())));

                        }

                        setLineChart(humidEntries, envHumidChart, 1);
                        setLineChart(tempEntries, envTempChart, 2);
                        setLineChart(loudEntries, envLoudChart, 3);
                        setLineChart(lightEntries, envLightChart, 4);

                        progressDialog.dismiss();

                    } else {
                        Log.e(getClass().toString(), new Gson().toJson(response.body()));
                        Toast.makeText(getContext(), " 수면데이터가 존재하지 않습니다. 우측하단의 아이콘을 클릭하여 다른 날짜를 선택해주세요.", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }

                } else {
                    Log.e(getClass().toString(), new Gson().toJson(response.errorBody()));
                }
            }


            @Override
            public void onFailure(Call<EnvResult> call, Throwable t) {
                Toast.makeText(getContext(), "에러가 발생했습니다.", Toast.LENGTH_LONG).show();
                Log.e(getClass().toString(), t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void setLineChart(List<Entry> entries, LineChart envChart, int mode) {

        LineDataSet dataSet = new LineDataSet(entries, "hello");
        dataSet.setDrawFilled(true);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        dataSet.setCubicIntensity(0.2f);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(1.8f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(Color.WHITE);
        switch (mode) {
            case 1:
                dataSet.setColor(Color.rgb(104, 241, 175));
                dataSet.setFillColor(Color.rgb(104, 241, 175));
                break;
            case 2:
                dataSet.setColor(Color.rgb(20, 190, 204));
                dataSet.setFillColor(Color.rgb(20, 190, 204));
                break;
            case 3:
                dataSet.setColor(Color.rgb(255, 64, 80));
                dataSet.setFillColor(Color.rgb(255, 64, 80));
                break;
            case 4:
                dataSet.setColor(Color.rgb(204, 20, 128));
                dataSet.setFillColor(Color.rgb(204, 20, 128));
                break;
        }
        dataSet.setFillAlpha(255);
        dataSet.setDrawVerticalHighlightIndicator(false);
        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -10;
            }
        });
        dataSet.notifyDataSetChanged();
        LineData lineData = new LineData(dataSet);
        lineData.setDrawValues(false);
        lineData.notifyDataChanged();


        envChart.setData(lineData);
        envChart.getLegend().setEnabled(false);
        envChart.setMaxVisibleValueCount(30);
        envChart.notifyDataSetChanged();
        envChart.invalidate();
        envChart.zoom(4,1, 0, 0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Toast.makeText(getContext(), year + "년" + monthOfYear + "월" + dayOfMonth + "일", Toast.LENGTH_SHORT).show();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, monthOfYear, dayOfMonth);

//
        renderDefaultChart(email, envService, selectedDate);

    }

    private void initializeChart(LineChart envChart) {
        envChart.setBackgroundColor(Color.WHITE);
        envChart.getDescription().setEnabled(false);
        envChart.setDrawGridBackground(false);
        // enable scaling and dragging
        envChart.setDragEnabled(true);
        envChart.setScaleEnabled(true);
        envChart.setDoubleTapToZoomEnabled(false);
        envChart.setVisibleXRangeMaximum(30);
        // if disabled, scaling can be done on x- and y-axis separately
        envChart.setPinchZoom(false);
        envChart.setTouchEnabled(true);
        EnvMarkerView marker = new EnvMarkerView(getContext(), R.layout.markerview);
        marker.setChartView(envChart);
        envChart.setMarker(marker);
        XAxis x = envChart.getXAxis();
        x.setTextColor(Color.BLACK);
        x.setDrawGridLines(false);
        x.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MINUTE, (int) value);
                return cal.get(Calendar.HOUR) + "시 " + cal.get(Calendar.MINUTE) + "분";
            }
        });

        YAxis y = envChart.getAxisLeft();

        y.setLabelCount(6, false);
        y.setTextColor(Color.BLACK);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.BLACK);


        envChart.getAxisRight().setEnabled(false);
    }


    @Override
    public void onClick(View view) {

        ProgressDialog progressDialog = ProgressUtils.showProgressDialog(getContext(), "데이터 읽어오는 중..");
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                EnvFragment.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setAccentColor(getResources().getColor(R.color.primaryColor));
        Call<EnvResult> result = envService.getSelectableDays(email);
        result.enqueue(new Callback<EnvResult>() {
            @Override
            public void onResponse(Call<EnvResult> call, Response<EnvResult> response) {
                if (response.isSuccessful()) {
                    List<UserEnv> userEnvList = response.body().getUserEnv();
                    List<Calendar> calendarList = new ArrayList<>();
                    for (UserEnv userEnv : userEnvList) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                        Date date = null;
                        try {
                            date = sdf.parse(userEnv.getEnvTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        if (!calendarList.contains(cal)) {
                            calendarList.add(cal);
                        }
                    }
                    Calendar[] selectableDays = new Calendar[calendarList.size()];
                    Log.i(getClass().toString(), "calendarList >> " + calendarList.toString());
                    dpd.setSelectableDays(calendarList.toArray(selectableDays));
                }
                progressDialog.dismiss();
                dpd.show(getActivity().getFragmentManager(), "DatePickerDialog");
            }

            @Override
            public void onFailure(Call<EnvResult> call, Throwable t) {
                Toast.makeText(getContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                Log.e(getClass().toString(), t.getMessage());
                t.printStackTrace();
                progressDialog.dismiss();
            }
        });

    }
}
