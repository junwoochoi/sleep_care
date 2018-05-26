package com.example.dell.sleepcare.Activitity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.dell.sleepcare.NaverHandler;
import com.example.dell.sleepcare.R;
import com.example.dell.sleepcare.RESTAPI.LoginService;
import com.example.dell.sleepcare.RegisterDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.dell.sleepcare.Utils.Constants.API_URL;
import static com.example.dell.sleepcare.Utils.Constants.GOOGLE_CLIENT_ID;
import static com.example.dell.sleepcare.Utils.Constants.NAVER_CLIENT_ID;
import static com.example.dell.sleepcare.Utils.Constants.NAVER_SECRET;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.button_naverlogin)
    OAuthLoginButton buttonOAuthLoginImg;
    @BindView(R.id.button_facebook)
    LoginButton fbButton;
    @BindView(R.id.google_login)
    Button googleBtn;
    @BindView(R.id.layout_background)
    RelativeLayout relativeLayout;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.button_naver)
    Button naverBtn;
    GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Retrofit retrofit;
    LoginService loginService;
    FirebaseAuth mAuth;
    RegisterDialog dialog;
    static final int SIGN_IN = 9001;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        relativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        //상단바와 하단네비바 색상 지정
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)); //status bar or the time bar at the top
        }
        sp = getSharedPreferences("userData", MODE_PRIVATE);
        editor = sp.edit();

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        OAuthLogin oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(
                this
                , NAVER_CLIENT_ID
                , NAVER_SECRET
                , "SleepCare"
        );


            //네이버 로그인 세팅
            buttonOAuthLoginImg.setOAuthLoginHandler(new NaverHandler(this, oAuthLogin, this));

            //구글 로그인 세팅
            setGoogleLogin();
            googleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, SIGN_IN);
                }
            });

            //페이스북 로그인 세팅
            callbackManager = CallbackManager.Factory.create();
            fbButton.setReadPermissions("email");
            fbButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // App code
                            if (response.getError() != null) {
                                // handle error
                                Log.e("Response error", response.getError().toString());
                            } else {
                                Log.d("getId()", String.valueOf(Profile.getCurrentProfile().getId()));
                                Log.d("getName()", String.valueOf(Profile.getCurrentProfile().getName())); // 이름
                                editor.putString("name", String.valueOf(Profile.getCurrentProfile().getName()));
                                editor.putString("email", object.optString("email"));
                                editor.apply();
                                checkLogin(object.optString("email"));
                            }
                        }
                    });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "name,email");
                    request.setParameters(parameters);
                    request.executeAsync();


                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });
        }


    //구글 로그인 세팅
    private void setGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder
                (GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.e("hello", "google login failed!!!");
                            }
                        } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("googlelgoin", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            editor.putString("name", acct.getDisplayName().toString());
            editor.putString("email", acct.getEmail().toString());
            editor.apply();
            checkLogin(acct.getEmail().toString());
        } else {
            // Signed out, show unauthenticated UI.
            Log.e("googlelogin", "signed out");
        }
    }

    public void onClick(View v) {
        if (v == naverBtn) {
            buttonOAuthLoginImg.performClick();
        } else {
            fbButton.performClick();
        }
    }

    public void checkLogin(String email){
        retrofit = new Retrofit.Builder().baseUrl(API_URL).addConverterFactory(GsonConverterFactory.create()).build();
        loginService = retrofit.create(LoginService.class);
        final Call<com.example.dell.sleepcare.RESTAPI.LoginResult> res = loginService.login(email);
        res.enqueue(new Callback<com.example.dell.sleepcare.RESTAPI.LoginResult>() {
            @Override
            public void onResponse(Call<com.example.dell.sleepcare.RESTAPI.LoginResult> call, Response<com.example.dell.sleepcare.RESTAPI.LoginResult> response) {
                if(response.isSuccessful()){
                    com.example.dell.sleepcare.RESTAPI.LoginResult res = response.body();
                    if (res != null) {
                        if(res.getLoginResult().equals("200")) {
                            Toast.makeText(getApplicationContext(), "데이터에 이미 등록됨!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "데이터 등록이 필요합니다!", Toast.LENGTH_LONG).show();
                            RegisterDialog dialog = new RegisterDialog(LoginActivity.this, getFragmentManager(), sp.getString("name",""), sp.getString("email",""));
                            dialog.show();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<com.example.dell.sleepcare.RESTAPI.LoginResult> call, Throwable t) {
                Log.e("Network Call failed", "데이터 받아오기 실패");
                Toast.makeText(getApplicationContext(),"데이터 받아오기 실패! 서버가 응답하지 않습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }


}


