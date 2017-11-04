package com.congp.finkids.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.congp.finkids.R;
import com.congp.finkids.event.OnPhotoListenner;
import com.congp.finkids.ui.profile.ProfileActivity;
import com.congp.finkids.ui.regis.RegisActivity;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

/**
 * Created by congp on 11/4/2017.
 */

public class DialogUtils {
    ProgressDialog dialog;
    Activity activity;

    public DialogUtils(ProgressDialog dialog, Activity activity) {
        this.dialog = dialog;
        this.activity = activity;
    }

    public void hideLoading() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
    public static void showInfor(Context context, String infor) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(infor);
        builder.setIcon(R.drawable.logo_app);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        builder.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public  void showLoading() {
        if (dialog != null) {
            if (dialog.isShowing()) dialog.dismiss();
            dialog.show();
            return;
        }
        dialog = ProgressDialog
                .show(activity, "", "Vui lòng đợi ...", true);
    }

    public static void showDialogGetPhotoMenu(Activity activity, OnPhotoListenner listenner) {
        CharSequence[] items = {"Chọn hình", "Chụp hình"};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.replayavatar));
        builder.setIcon(R.drawable.ic_no_image);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Chọn hình")) {
                    listenner.choossePhoto();
                } else if (items[i].equals("Chụp hình")) {
                    listenner.takePhoto();
                }
            }
        });
        builder.setCancelable(true);
        final android.support.v7.app.AlertDialog dialog = builder.create();
        builder.setNegativeButton("hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
