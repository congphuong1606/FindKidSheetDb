package com.congp.finkids.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.congp.finkids.R;
import com.congp.finkids.base.BaseActivity;
import com.congp.finkids.common.Constants;
import com.congp.finkids.ui.login.LoginActivity;
import com.congp.finkids.ui.main.MainActivity;

public class SplashActivity extends BaseActivity {
    private SharedPreferences preferences;
    private String email;
    private String pass;
    private String loginedstamp;

    @Override
    protected int getContentLayoutID() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData() {
        preferences = getSharedPreferences(Constants.SPF_NAME, Context.MODE_PRIVATE);
        email = preferences.getString("emaillogin", "");
        pass = preferences.getString("passlogin", "");
        loginedstamp = preferences.getString("loginedstamp", "");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(loginedstamp.equals("đã đăng nhập")){
                    onStartActivity(MainActivity.class);
                    finish();
                }else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    i.putExtra("email", email);
                    i.putExtra("pass", pass);
                    i.putExtra("activity", "splash");
                    startActivity(i);
                    finish();
                }
                finish();
            }
        }, 1000);
    }

    @Override
    protected void injectDependence() {

    }
}
