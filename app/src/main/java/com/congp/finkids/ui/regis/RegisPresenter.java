package com.congp.finkids.ui.regis;

import com.congp.finkids.common.Constants;
import com.congp.finkids.data.dto.Result;
import com.congp.finkids.network.ApiService;
import com.congp.finkids.network.ApiUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ominext on 10/2/2017.
 */

public class RegisPresenter {
    private RegisView mRegisView;
    private ApiService mApiService;

    public RegisPresenter(RegisView mRegisView) {
        this.mRegisView = mRegisView;
        this.mApiService = ApiUtils.getIapiService();
    }


    public void onSignUp(String email, String pass) {
        String userName=email.split("@")[0];
        mApiService.creatUser("insert-user",email,pass,userName,"").subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onFail);
    }
    private void onSuccess(Result result) {
        if(result.getResult().equals(Constants.INSERT_EXIST)){
            mRegisView.onRequestFail(Constants.EXIST);
        }
        if(result.getResult().equals(Constants.INSERT_SUCCESS)){
            mRegisView.onSignUpSuccess();
        }


    }
    private void onFail(Throwable throwable) {
        mRegisView.onRequestFail( String.valueOf(throwable));
    }
}
