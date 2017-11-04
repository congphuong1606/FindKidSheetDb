package com.congp.finkids.ui.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.congp.finkids.ui.main.MainActivity;
import com.congp.finkids.R;
import com.congp.finkids.base.BaseActivity;
import com.congp.finkids.common.CheckInput;
import com.congp.finkids.common.Constants;
import com.congp.finkids.common.DialogUtils;
import com.congp.finkids.data.User;
import com.congp.finkids.ui.regis.RegisActivity;

import butterknife.BindView;
import butterknife.OnClick;


public class LoginActivity extends BaseActivity implements LoginView {
    private ProgressDialog dialog;
    private DialogUtils dialogUtils;
    @BindView(R.id.edtLoginEmail)
    EditText edtLoginEmail;
    @BindView(R.id.edtLoginPass)
    EditText edtLoginPass;
    @BindView(R.id.Remember)
    CheckBox Remember;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.btnRegis)
    Button btnRegis;
    @BindView(R.id.btnQuit)
    Button btnQuit;
    private LoginPresenter mLoginPresenter;
    private String email;
    private String pass;
    private SharedPreferences.Editor editor;

    @Override
    protected int getContentLayoutID() {
        return R.layout.activity_login;

    }

    @Override
    protected void initData() {
        setDataEditText();
        dialogUtils=new DialogUtils(dialog,this);
        mLoginPresenter = new LoginPresenter(this);
        editor = getSharedPreferences(Constants.SPF_NAME, Context.MODE_PRIVATE).edit();
    }

    private void setDataEditText() {
        Intent intent=getIntent();
        email=intent.getStringExtra("email");
        pass=intent.getStringExtra("pass");
        String activity=intent.getStringExtra("activity");
        edtLoginEmail.setText(email);
        edtLoginPass.setText(pass);
        if(!email.equals("")&&!pass.equals("")){
            Remember.setChecked(true);
        }
        if(activity.equals("regis")){
            onShowToast(Constants.REGIS_SUCCESS);
        }
    }




    @Override
    protected void injectDependence() {

    }

    private void login() {
        if (CheckInput.checkInPutLogin(edtLoginEmail, edtLoginPass, this)) {
            email = edtLoginEmail.getText().toString().trim();
            pass = edtLoginPass.getText().toString().trim();
            dialogUtils.showLoading();
            mLoginPresenter.onLogin(email, pass);

        }
    }

    @Override
    public void onSiginSuccess(User user) {
        dialogUtils.hideLoading();
        if (Remember.isChecked()) {
            editor.putString("emaillogin", email)
                    .putString("passlogin", pass).commit();
        } else {
            editor.clear();
        }
        editor.putString("email", user.getEmail())
                .putString("pass", user.getPass())
                .putString("name", user.getName())
                .putString("avatar", user.getAvatar())
                .putString("loginedstamp", "đã đăng nhập")
                .commit();
        onStartActivity(MainActivity.class);
        finish();
    }

    @Override
    public void onRequestFail(String s) {
        dialogUtils.hideLoading();
        DialogUtils.showInfor(this,s);
    }



    @OnClick({R.id.btnLogin, R.id.btnRegis, R.id.btnQuit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnRegis:
                onStartActivity(RegisActivity.class);
                finish();
                break;
            case R.id.btnQuit:
                finish();
                break;
        }
    }
}
