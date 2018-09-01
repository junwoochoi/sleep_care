package com.example.dell.sleepcare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.dell.sleepcare.Activitity.LoginActivity;
import com.example.dell.sleepcare.Activitity.MainActivity;
import com.example.dell.sleepcare.RESTAPI.LoginResult;
import com.example.dell.sleepcare.RESTAPI.LoginService;
import com.example.dell.sleepcare.RESTAPI.RetrofitClient;
import com.example.dell.sleepcare.Utils.SharedPrefUtils;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.dell.sleepcare.Utils.Constants.API_URL;

public class NaverHandler extends OAuthLoginHandler {

    private Context mContext;
    private LoginActivity activity;
    private OAuthLogin mOAuthLoginModule;
    SharedPreferences sp;


    public NaverHandler(Context mContext, OAuthLogin mOAuthLoginModule, LoginActivity activity) {
        this.mContext = mContext;
        this.mOAuthLoginModule = mOAuthLoginModule;
        this.activity = activity;
    }

    @Override
    public void run(boolean success) {
        if (success) {
            final String accessToken = mOAuthLoginModule.getAccessToken(mContext);
            // 얘가 있어야 유저 정보를 가져올 수 있습니다.
            ProfileTask task = new ProfileTask();
            // 이 클래스가 유저정보를 가져오는 업무를 담당합니다.
            task.execute(accessToken);
        } else {
            String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
            String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
            Toast.makeText(mContext, "errorCode:" + errorCode
                    + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
        }
    }

    class ProfileTask extends AsyncTask<String, Void, String> {
        String result;
        @Override
        protected String doInBackground(String... strings) {
            String token = strings[0];// 네이버 로그인 접근 토큰;
            String header = "Bearer " + token; // Bearer 다음에 공백 추가
            try {
                String apiURL = "https://openapi.naver.com/v1/nid/me";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", header);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                result = response.toString();
                br.close();
                System.out.println(response.toString());
            } catch (Exception e) {
                System.out.println(e);
            }
            //result 값은 JSONObject 형태로 넘어옵니다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //넘어온 result 값을 JSONObject 로 변환해주고, 값을 가져온다.

                JSONObject object = new JSONObject(result);
                if(object.getString("resultcode").equals("00")) {
                    JSONObject jsonObject = new JSONObject(object.getString("response"));
                    Log.d("jsonObject", jsonObject.toString());
                    sp = SharedPrefUtils.getInstance(activity).getPrefs();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("email", jsonObject.getString("email"));
                    editor.putString("name", jsonObject.getString("name"));
                    editor.apply();

                    checkLogin(jsonObject.getString("email"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void checkLogin(final String email){
        Retrofit retrofit = RetrofitClient.getClient(API_URL);
        LoginService loginService = retrofit.create(LoginService.class);
        final Call<com.example.dell.sleepcare.RESTAPI.LoginResult> res = loginService.login(email);
        res.enqueue(new retrofit2.Callback<LoginResult>() {
            @Override
            public void onResponse(Call<com.example.dell.sleepcare.RESTAPI.LoginResult> call, Response<LoginResult> response) {
                if(response.isSuccessful()){
                    com.example.dell.sleepcare.RESTAPI.LoginResult res = response.body();
                    if (res != null) {
                        if(res.getLoginResult().equals("200")) {
                            Toast.makeText(mContext, "데이터에 이미 등록됨!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mContext.startActivity(intent);
                        } else {
                            Toast.makeText(mContext, "데이터를 등록이 필요합니다!", Toast.LENGTH_LONG).show();
                            Toast.makeText(mContext, "데이터 등록이 필요합니다!", Toast.LENGTH_LONG).show();
                            RegisterDialog dialog = new RegisterDialog(mContext, ((Activity) mContext).getFragmentManager(), sp.getString("name",""), email);
                            dialog.show();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<com.example.dell.sleepcare.RESTAPI.LoginResult> call, Throwable t) {
                Log.e("Network Call failed", "데이터 받아오기 실패");
                Toast.makeText(mContext,"데이터 받아오기 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

}