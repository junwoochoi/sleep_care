package com.example.dell.sleepcare.RESTAPI;
public class LoginResult
{
    String login_res; // 이 변수명들은 반드시 json의 'key name'이랑 같아야 한다!!!!
    String register_res;
    int stamp_res;

    public String getRegisterResult() {
        return register_res;
    }

    public String getLoginResult() // method명은 상관 없음
    {
        return login_res;
    }

    public int getNumOfStamp() // method명은 상관 없음
    {
        return stamp_res;
    }
}
