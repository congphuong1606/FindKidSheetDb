package com.congp.finkids.ui.profile;

import android.support.annotation.NonNull;

import com.congp.finkids.common.Constants;
import com.congp.finkids.data.dto.Result;
import com.congp.finkids.network.ApiService;
import com.congp.finkids.network.ApiUtils;
import com.congp.finkids.ui.main.MainView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by congp on 11/4/2017.
 */

public class ProfilePresenter {
    private final FirebaseAuth mAuth;
    private ApiService mApiService;
    private ProfileView profileView;
    private StorageReference mStorageReference;


    public ProfilePresenter(ProfileView profileView) {
        this.mApiService = ApiUtils.getIapiService();
        this.profileView = profileView;
        mAuth = FirebaseAuth.getInstance();
        mStorageReference= FirebaseStorage.getInstance().getReference();

    }

    public void resetPassword(String email, String oldPass, String newPass) {
        mApiService.updatePass("update-pass", email, oldPass, newPass).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onFail);
    }

    private void onFail(Throwable throwable) {
        profileView.onFail(String.valueOf(throwable));
    }

    private void onSuccess(Result result) {
        if(result.getResult().equals("Mật khẩu không đúng")){
            profileView.onFail(String.valueOf(result.getResult()));
        }else {
            profileView.onUpdatePassSuccess(result.getResult());
        }

    }

    public void replaceAccName(String email, String name) {
        mApiService.updateName("update-name", email, name).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onupdateNameSuccess, this::onFail);
    }

    private void onupdateNameSuccess(Result result) {
        profileView.onupdateNameSuccess(result.getResult());
    }

    public void loginFireBase() {
        mAuth.signInAnonymously();
    }
    public void updateAvatar(byte[] bytes) {
        String picName = String.valueOf(System.currentTimeMillis());
        StorageReference mSto = mStorageReference.child(Constants.IMAGE_PIC_PATH).child(picName + ".jpg");
        UploadTask uploadTask = mSto.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String avatarUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                profileView.onUploadPicSuccess(avatarUrl);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                profileView.onFail(String.valueOf(exception));
            }
        });
    }

    public void updateAvatar(String email, String avatarUrl) {
        mApiService.updateAvatar("update-avatar", email, avatarUrl).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onupdateAvatarSuccess, this::onFail);
    }

    private void onupdateAvatarSuccess(Result result) {
        profileView.onUpdateAvatarSuccess(result.getResult());
    }
}
