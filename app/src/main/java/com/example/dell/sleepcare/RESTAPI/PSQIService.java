package com.example.dell.sleepcare.RESTAPI;

import com.example.dell.sleepcare.Model.PSQIResult;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static com.example.dell.sleepcare.Utils.Constants.API_URL;

public interface PSQIService {
    @GET(API_URL + "psqi/{user_email}")
    Call<List<PSQIResult>> getPSQIList(@Path("user_email") String email);

    @FormUrlEncoded
    @POST(API_URL + "psqiresult/")
    Call<LoginResult> sendPSQI(@FieldMap HashMap<String, Integer> psqiResults,
                               @Field("user_email") String userEmail);
}
