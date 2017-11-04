package com.congp.finkids.ui.login;

import com.congp.finkids.data.User;

/**
 * Created by Ominext on 10/2/2017.
 */

public interface LoginView {
    void onSiginSuccess(User user);
    void onRequestFail(String s);
}
