package com.congp.finkids.ui.main;

import android.content.Context;

import com.congp.finkids.common.Constants;
import com.congp.finkids.data.User;
import com.congp.finkids.data.dto.Result;
import com.congp.finkids.network.ApiService;
import com.congp.finkids.network.ApiUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by congp on 11/4/2017.
 */

public class MainPresenter {
    private ApiService mApiService;
    private MainView mainView;
    private String Uemail;

    public MainPresenter(MainView mainView,String e) {
        this.mApiService = ApiUtils.getIapiService();
        this.mainView = mainView;
        Uemail=e;

    }

    public void findUserEmail(String email) {
        mApiService.findUser("find-user", email).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onFail);
    }

    private void onFail(Throwable throwable) {
        mainView.onRequestFail(throwable.toString());
    }

    private void onSuccess(User user) {
        if(user.getEmail().equals("")){
            mainView.onRequestFail("Không tồn tại email");
        }else {
            mainView.onFindUserSuccess(user);
        }

    }

    public void addMemberToCricle(String email) {
        mApiService.addMemberToCricle("add-member-to-circle",Uemail, email).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onaddMemberSuccess, this::onFail);
    }

    private void onaddMemberSuccess(Result result) {
        mainView.onAddMemberToCricleSussess(result.getResult());
    }
}
