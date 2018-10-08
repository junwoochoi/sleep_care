package com.example.dell.sleepcare.RESTAPI;

import com.example.dell.sleepcare.Model.EnvResult;
import com.example.dell.sleepcare.Model.UserEnv;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static com.example.dell.sleepcare.Utils.Constants.API_URL;

public interface EnvService {
    @GET(API_URL + "env/{user_email}")
    Call<EnvResult> getEnvList(@Path("user_email") String email);

    @FormUrlEncoded
    @POST(API_URL + "psqiresult/")
    Call<RESTResponse> sendEnv(@Field("user_env") UserEnv userEnv);
}
