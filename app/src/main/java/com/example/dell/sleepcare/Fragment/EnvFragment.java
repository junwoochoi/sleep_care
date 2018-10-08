package com.example.dell.sleepcare.Fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dell.sleepcare.Model.EnvResult;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.RESTAPI.EnvService;
import com.example.dell.sleepcare.RESTAPI.RetrofitClient;
import com.example.dell.sleepcare.Utils.Constants;
import com.example.dell.sleepcare.Utils.SharedPrefUtils;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class EnvFragment extends Fragment implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    Unbinder unbinder;
    FloatingActionButton calendarFbBtn;


    public EnvFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_env, container, false);
        calendarFbBtn = (FloatingActionButton) view.findViewById(R.id.calendar_fb_btn);
        calendarFbBtn.setOnClickListener(this);
        unbinder = ButterKnife.bind(view);

        String email = SharedPrefUtils.getInstance(getContext()).getStringExtra("email");
        Retrofit retrofit = RetrofitClient.getClient(Constants.API_URL);
        EnvService envService = retrofit.create(EnvService.class);
        Call<EnvResult> resultCall = envService.getEnvList(email);
        resultCall.enqueue(new Callback<EnvResult>() {
            @Override
            public void onResponse(Call<EnvResult> call, Response<EnvResult> response) {
                if (response.isSuccessful()) {
                    EnvResult body = response.body();
                    Log.i(getClass().toString(), "response Success!! >>");
                    Log.i(getClass().toString(), new Gson().toJson(response.body()));


                } else {
                    Log.e(getClass().toString(), new Gson().toJson(response.errorBody()));
                }
            }


            @Override
            public void onFailure(Call<EnvResult> call, Throwable t) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                Log.e(getClass().toString(), t.getMessage());
                t.printStackTrace();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }




    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Toast.makeText(getContext(), year + "년" + monthOfYear + "월" + dayOfMonth + "일", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onClick(View view) {
        Toast.makeText(getContext(), "클릭", Toast.LENGTH_SHORT).show();
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                EnvFragment.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setAccentColor(getResources().getColor(R.color.primaryColor));

        dpd.show(getActivity().getFragmentManager(), "DatePickerDialog");
    }
}
