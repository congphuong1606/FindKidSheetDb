package com.congp.finkids.ui.regis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.congp.finkids.R;
import com.congp.finkids.base.BaseActivity;
import com.congp.finkids.common.CheckInput;
import com.congp.finkids.common.DialogUtils;
import com.congp.finkids.ui.login.LoginActivity;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisActivity extends BaseActivity implements RegisView {
    private ProgressDialog dialog;
    private DialogUtils dialogUtils;
    @BindView(R.id.edtRegisEmail)
    EditText edtRegisEmail;
    @BindView(R.id.edtRegisPass)
    EditText edtRegisPass;
    @BindView(R.id.edtConfirmPass)
    EditText edtConfirmPass;
    @BindView(R.id.btnSignUp)
    Button btnSignUp;
    @BindView(R.id.btnRegisBack)
    Button btnRegisBack;
    private RegisPresenter mPresenter;
    private String email;
    private String pass;


    @Override
    protected int getContentLayoutID() {
        return R.layout.activity_regis;
    }

    @Override
    protected void initData() {
       dialogUtils=new DialogUtils(dialog,this);
        mPresenter = new RegisPresenter(this);
    }

    @Override
    protected void injectDependence() {

    }

    @OnClick({R.id.btnSignUp, R.id.btnRegisBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                signUpAction();
                break;
            case R.id.btnRegisBack:
                onBackPressed();
                break;
        }
    }

    private void signUpAction() {
        if (CheckInput.checkInPutRegis(
                edtRegisEmail, edtRegisPass,
                edtConfirmPass, this)) {
//            dialogUtils.showLoading()
            email = edtRegisEmail.getText().toString().trim();
            pass = edtRegisPass.getText().toString().trim();
            dialogUtils.showLoading();
            mPresenter.onSignUp(email, pass);
        }
    }

    @Override
    public void onSignUpSuccess() {
        dialogUtils.hideLoading();
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("email", email);
        i.putExtra("pass", pass);
        i.putExtra("activity", "regis");
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        onStartActivity(LoginActivity.class);
        finish();
    }

    @Override
    public void onRequestFail(String s) {
        dialogUtils.hideLoading();
        DialogUtils.showInfor(this,s);

    }
}
