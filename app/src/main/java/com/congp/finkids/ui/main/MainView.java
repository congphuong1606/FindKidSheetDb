package com.congp.finkids.ui.main;

import com.congp.finkids.data.User;

/**
 * Created by congp on 11/4/2017.
 */

public interface MainView {
    void onFindUserSuccess(User user);
    void onRequestFail(String s);
    void onAddMemberToCricleSussess(String result);
}
