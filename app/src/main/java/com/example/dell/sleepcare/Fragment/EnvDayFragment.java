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

import com.example.dell.sleepcare.Model.EnvDayResult;
import com.example.dell.sleepcare.Model.UserEnvDay;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.RESTAPI.EnvService;
import com.example.dell.sleepcare.RESTAPI.RetrofitClient;
import com.example.dell.sleepcare.Utils.Constants;
import com.example.dell.sleepcare.Utils.ProgressUtils;
import com.example.dell.sleepcare.Utils.SharedPrefUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class EnvDayFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    EnvService envService;
    FloatingActionButton calendarFbBtn;
    BarChart barChart;
    Unbinder unbinder;
    Retrofit retrofit;
    String email;
    public EnvDayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_env_day, container, false);
        unbinder = ButterKnife.bind(view);
        email = SharedPrefUtils.getInstance(getContext()).getStringExtra("email");

        retrofit = RetrofitClient.getClient(Constants.API_URL);

        envService = retrofit.create(EnvService.class);
        barChart = view.findViewById(R.id.env_day_chart);
        calendarFbBtn = (FloatingActionButton) view.findViewById(R.id.calendar_day_fb_btn);
        calendarFbBtn.setOnClickListener(this);

        initializeChart();

        return view;
    }

    private void initializeChart() {
        YAxis leftAxis = barChart.getAxisLeft();
        YAxis rightAxis = barChart.getAxisRight();
        XAxis xAxis = barChart.getXAxis();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);


        leftAxis.setTextSize(10f);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawGridLines(false);

        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawLabels(false);





        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate(); // refresh
        barChart.setScaleEnabled(false);
        barChart.setBackgroundColor(Color.rgb(255, 255, 255));
        barChart.animateXY(2000, 2000);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart.setDrawBorders(false);
        Description desc = new Description();
        desc.setText("수면정보");
        barChart.setDescription(desc);
        barChart.setDrawValueAboveBar(true);

    }


    @Override
    public void onClick(View view) {
        ProgressDialog progressDialog = ProgressUtils.showProgressDialog(getContext(), "데이터 읽어오는 중..");
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                EnvDayFragment.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setAccentColor(getResources().getColor(R.color.primaryColor));
        Call<List<Map<String, String>>> result = envService.getSelectableDaysFromDay(email);
        result.enqueue(new Callback<List<Map<String, String>>>() {
            @Override
            public void onResponse(Call<List<Map<String, String>>> call, Response<List<Map<String, String>>> response) {
                if (response.isSuccessful()) {
                    List<Calendar> calendarList = new ArrayList<>();
                    List<Map<String, String>> userEnvDayList  = response.body();
                    for (Map<String, String> userDate : userEnvDayList) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                        Date date = null;
                        try {
                            date = sdf.parse(userDate.get("ENV_DAY_TIME"));
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
            public void onFailure(Call<List<Map<String, String>>> call, Throwable t) {
                Log.e(getClass().toString(), t.getMessage());
                Toast.makeText(getContext(), "날짜 받아오기 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                Toast.makeText(getContext(), year + "년" + monthOfYear + "월" + dayOfMonth + "일", Toast.LENGTH_SHORT).show();
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);

//
                renderChart(email, envService, selectedDate);

            }

    private void renderChart(String email, EnvService envService, Calendar selectedDate) {
        ProgressDialog progressDialog = ProgressUtils.showProgressDialog(getContext(), "데이터 불러오는 중...");
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH) + 1;
        int dateOfMonth = selectedDate.get(Calendar.DATE);
        String monthStr = String.valueOf(month);
        String dateOfMonthStr = String.valueOf(dateOfMonth);
        if (month < 10) {
            monthStr = "0" + monthStr;
        }
        if (dateOfMonth < 10) {
            dateOfMonthStr = "0" + dateOfMonthStr;
        }

        String selectedDateStr = selectedDate.get(Calendar.YEAR) + "-"
                + monthStr + "-"
                + dateOfMonthStr;
        Call<EnvDayResult> resultCall = envService.getEnvDayList(email, selectedDateStr);

        resultCall.enqueue(new Callback<EnvDayResult>() {
            @Override
            public void onResponse(Call<EnvDayResult> call, Response<EnvDayResult> response) {
                    progressDialog.dismiss();
                if (response.isSuccessful()) {
                    EnvDayResult body = response.body();
                    Log.i(getClass().toString(), "response Success!! >>");
                    Log.i(getClass().toString(), new Gson().toJson(body));
                    UserEnvDay envDay = body.getUserEnvDay();
                    ArrayList<BarEntry> entries = new ArrayList<>();

                    entries.add(new BarEntry(1f, Float.parseFloat(envDay.getEnvDayMove())));
                    entries.add(new BarEntry(2f, Float.parseFloat(envDay.getEnvDaySleeptime())));
                    entries.add(new BarEntry(3f, Float.parseFloat(envDay.getEnvDaySnore())));

                    BarDataSet dataSet = new BarDataSet(entries, "");
                    dataSet.setColor(getResources().getColor(R.color.colorAccent));
                    
                    BarData barData = new BarData(dataSet);
                    barChart.setData(barData);
                    barChart.invalidate();

                } else {
                    Log.e(getClass().toString(), new Gson().toJson(response.body()));
                    Toast.makeText(getContext(), " 수면데이터가 존재하지 않습니다. 우측하단의 아이콘을 클릭하여 다른 날짜를 선택해주세요.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<EnvDayResult> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "에러가 발생했습니다.", Toast.LENGTH_LONG).show();
                Log.e(getClass().toString(), t.getMessage());
                t.printStackTrace();
            }
        });
    }
}