package com.congp.finkids.ui.profile;

/**
 * Created by congp on 11/4/2017.
 */

public interface ProfileView {
    void onFail(String s);

    void onUpdatePassSuccess(String result);

    void onupdateNameSuccess(String result);

    void onUploadPicSuccess(String avatarUrl);

    void onUpdateAvatarSuccess(String result);
}
