package com.example.dell.sleepcare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.RESTAPI.LoginResult;
import com.example.dell.sleepcare.RESTAPI.LoginService;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.dell.sleepcare.Utils.Constants.API_URL;

public class RegisterDialog extends Dialog implements DatePickerDialog.OnDateSetListener {
    private static final int LAYOUT = R.layout.dialog_custom;
    @BindView(R.id.gender_select)
    Spinner genderSelect;

    private Context context;
    private FragmentManager fragmentManager;
    private String userName, userEmail;
    private Retrofit retrofit;
    LoginService loginService;
    SharedPreferences sp;
    SharedPreferences.Editor edit;

    public RegisterDialog(@NonNull Context context, FragmentManager fragmentManager, String userName, String userEmail) {
        super(context);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    @BindView(R.id.button_confirm)
    TextView confirBtn;
    @BindView(R.id.button_cancel)
    TextView cancelBtn;
    @BindView(R.id.date_select)
    EditText dateSelect;
    @BindView(R.id.addr_select)
    Spinner addrSelect;
    @BindView(R.id.job_select)
    Spinner jobSelect;
    @BindView(R.id.edittext_name)
    EditText nameInput;

    Calendar now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(LAYOUT);
        setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);

        //SharedPreferences 초기화
        sp = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
        edit = sp.edit();
        //이름 초기화
        nameInput.setText(userName);

        //성별 선택지 초기화
        String[] genderItem = new String[]{"남성", "여성"};
        final ArrayAdapter genderAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, genderItem);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSelect.setAdapter(genderAdapter);

        //거주지 선택지 초기화
        String[] addrItem = new String[]{"서울", "부산", "대구", "인천", "광주", "대전", "경기", "강원", "충북", "충남", "전남", "전북", "경북", "경남", "제주"};
        final ArrayAdapter<String> addrAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, addrItem);
        addrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addrSelect.setAdapter(addrAdapter);

        //직업 선택지 초기화
        String[] jobItem = new String[]{"공무원", "교사", "회사원", "학생", "군인", "전업주부", "자영업", "서비스직", "기타"};
        final ArrayAdapter<String> jobAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, jobItem);
        jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobSelect.setAdapter(jobAdapter);

        now = Calendar.getInstance();
        dateSelect.setText(String.valueOf(now.get(Calendar.YEAR) + "-" + String.valueOf(now.get(Calendar.MONTH) + 1) + "-" + String.valueOf(now.get(Calendar.DAY_OF_MONTH))));

        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        RegisterDialog.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor("#7C50DB");
                dpd.show(fragmentManager, "Datepickerdialog");
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });
        confirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setTitle("확인");
                alertDialog.setMessage("\n입력한 정보로 가입하시겠습니까?");
                alertDialog.setButton(BUTTON_NEGATIVE, "취소", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.setButton(BUTTON_POSITIVE, "가입", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "가입메소드 호출", Toast.LENGTH_LONG).show();
                        registerDB();
                    }
                });
                alertDialog.show();
            }
        });

    }


    private void registerDB() {
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        loginService = retrofit.create(LoginService.class);
        userName = nameInput.getText().toString();
        final String userJob = jobSelect.getSelectedItem().toString();
        final String userAddr = addrSelect.getSelectedItem().toString();
        final String userDate = dateSelect.getText().toString();
        final String userGender = genderSelect.getSelectedItem().toString();

        final Call<LoginResult> res = loginService.register(userName, userEmail, userDate, userJob, userAddr, userGender);
        res.enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.isSuccessful()) {
                    LoginResult res = response.body();
                    if (res != null) {
                        if (res.getRegisterResult().equals("201")) {
                            Toast.makeText(context, "데이터에 성공적으로 등록됨!", Toast.LENGTH_LONG).show();
                            edit.clear();
                            edit.putString("name", userName);
                            edit.putString("email", userEmail);
                            edit.putString("birth", userDate);
                            edit.putString("job", userJob);
                            edit.putString("addr", userAddr);
                            edit.putString("gender", userGender);
                            edit.apply();
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "데이터 등록이 실패!", Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {

            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = String.valueOf(year) + "-" + String.valueOf(++monthOfYear) + "-" + String.valueOf(dayOfMonth);
        dateSelect.setText(date);
    }
}
