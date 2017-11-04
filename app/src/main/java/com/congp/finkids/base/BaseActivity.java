package com.congp.finkids.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by MyPC on 02/08/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder mUnbinder;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutID());
        mUnbinder = ButterKnife.bind(this);
        injectDependence();

        initData();
    }


    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroy();
    }
    protected abstract int getContentLayoutID();

    protected abstract void initData();


    protected abstract void injectDependence();


    public void onStartActivity(Class aClass) {
        startActivity(new Intent(this, aClass));
    }
    public void onShowToast(String string) {
        Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
    }

//    public void onShowBuiderPostPotion(final OnEventclickListener eventClick, Post post, int type,int position) {
//        CharSequence[] items = new CharSequence[0];
//        if (type == Constants.ANOTHER) {
//            items = new CharSequence[]{"lưu xem sau", "báo cáo"};
//        } else if (type == Constants.ME) {
//            items = new CharSequence[]{"xóa", "chỉnh sửa"};
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        CharSequence[] finalItems = items;
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int i) {
//                if (finalItems[i].equals("xóa")) {
//                    eventClick.onClickDelete(post.getPostId());
//                } else if (finalItems[i].equals("chỉnh sửa")) {
//                    eventClick.onClickEdit(post,position);
//                } else if (finalItems[i].equals("lưu xem sau")) {
//                    eventClick.onClickSavePostHistory(post.getPostId());
//                } else if (finalItems[i].equals("báo cáo")) {
//                    eventClick.onClickRePortPost(post);
//                }
//            }
//        });
//        AlertDialog dialog = builder.create();
//        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//        params.gravity = Gravity.BOTTOM;
//        dialog.getWindow().setAttributes(params);
//        dialog.show();
//    }









}



