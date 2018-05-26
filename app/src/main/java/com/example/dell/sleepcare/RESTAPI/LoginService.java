package com.example.dell.sleepcare.RESTAPI;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import static com.example.dell.sleepcare.Utils.Constants.API_URL;


public interface LoginService {


    @GET(API_URL+"login/{user_email}")
    Call<LoginResult> login(@Path("user_email") String email);

    @FormUrlEncoded
    @POST(API_URL+"register/")
    Call<LoginResult> register(@Field("user_name") String name,
                               @Field("user_email") String email,
                               @Field("user_birth") String birth,
                               @Field("user_job") String job,
                               @Field("user_addr") String addr,
                               @Field("user_gender")String gender
    );
}
