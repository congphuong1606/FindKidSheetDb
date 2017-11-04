package com.congp.finkids.ui.login;

import com.congp.finkids.common.Constants;
import com.congp.finkids.data.User;
import com.congp.finkids.network.ApiService;
import com.congp.finkids.network.ApiUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;



/**
 * Created by Ominext on 10/2/2017.
 */

public class LoginPresenter {
    private ApiService mApiService;
    private LoginView mLoginView;

    public LoginPresenter(LoginView mLoginView) {
        this.mApiService = ApiUtils.getIapiService();
        this.mLoginView = mLoginView;
    }

    public void onLogin(String email, String pass) {
        mApiService.login("login", email,pass).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onFail);
    }

    private void onSuccess(User user) {
        if(user.getEmail().equals("")){
            mLoginView.onRequestFail(Constants.LOGIN_FAIL);
        }else {
            mLoginView.onSiginSuccess(user);
        }
    }
    private void onFail(Throwable throwable) {
        if(String.valueOf(throwable).equals("com.google.gson.JsonSyntaxException: java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $")){
            mLoginView.onRequestFail(Constants.LOGIN_FAIL);
        }

    }
}
