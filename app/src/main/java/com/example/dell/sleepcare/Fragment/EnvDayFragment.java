package com.example.dell.sleepcare.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.RESTAPI.EnvService;
import com.example.dell.sleepcare.RESTAPI.RetrofitClient;
import com.example.dell.sleepcare.Utils.Constants;
import com.example.dell.sleepcare.Utils.ProgressUtils;
import com.example.dell.sleepcare.Utils.SharedPrefUtils;
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
        calendarFbBtn = (FloatingActionButton) view.findViewById(R.id.calendar_day_fb_btn);
        calendarFbBtn.setOnClickListener(this);


        return view;
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
    }
}