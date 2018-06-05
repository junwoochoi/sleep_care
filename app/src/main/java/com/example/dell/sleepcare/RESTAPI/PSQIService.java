package com.example.dell.sleepcare.RESTAPI;

import com.example.dell.sleepcare.Model.PSQIResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static com.example.dell.sleepcare.Utils.Constants.API_URL;

public interface PSQIService {
    @GET(API_URL+"psqi/{user_email}")
    Call<List<PSQIResult>> getPSQIList(@Path("user_email") String email);

    @FormUrlEncoded
    @POST(API_URL+"psqiresult/")
    Call<LoginResult> sendPSQI(@Field("comp1") int comp1,
                               @Field("comp2") int comp2,
                               @Field("comp3") int comp3,
                               @Field("comp4") int comp4,
                               @Field("comp5") int comp5,
                               @Field("comp6")int comp6,
                               @Field("comp7") int comp7,
                               @Field("user_email") String userEmail);
}
