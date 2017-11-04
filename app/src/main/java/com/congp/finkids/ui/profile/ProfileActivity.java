package com.congp.finkids.ui.profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.congp.finkids.R;
import com.congp.finkids.base.BaseActivity;
import com.congp.finkids.common.Constants;
import com.congp.finkids.common.DialogUtils;
import com.congp.finkids.event.OnPhotoListenner;
import com.congp.finkids.ui.SplashActivity;
import com.congp.finkids.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity implements ProfileView,OnPhotoListenner {
    ProfilePresenter presenter;
    private FirebaseAuth mAuth;
// ...

    @BindView(R.id.btnProfileBack)
    Button btnProfileBack;
    @BindView(R.id.imvAvatar)
    CircleImageView imvAvatar;
    @BindView(R.id.tvAccName)
    TextView tvAccName;
    @BindView(R.id.tvAccEmail)
    TextView tvAccEmail;
    @BindView(R.id.btnChangName)
    Button btnChangName;
    @BindView(R.id.btnChangePass)
    Button btnChangePass;
    @BindView(R.id.btnSignOut)
    Button btnSignOut;
    private SharedPreferences sPref;
    private String email;
    private String pass;
    private String name;
    private String avatar;
    private ProgressDialog dialog;
    private DialogUtils dialogUtils;
    private String newPass;
    private String newName;
    private String newAvatar;

    @Override
    protected int getContentLayoutID() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initData() {

        dialogUtils = new DialogUtils(dialog, this);
        presenter = new ProfilePresenter(this);
        presenter.loginFireBase();
        getInfor();
        setData();

    }

    private void setData() {
        tvAccEmail.setText(email);
        tvAccName.setText(name);
        Glide.with(this).load(avatar).error(getResources()
                .getDrawable(R.drawable.ic_no_image)).into(imvAvatar);
    }

    private void getInfor() {
        sPref = getSharedPreferences(Constants.SPF_NAME, Context.MODE_PRIVATE);
        email = sPref.getString("email", "");
        pass = sPref.getString("pass", "");
        avatar = sPref.getString("avatar", "");
        name = sPref.getString("name", "");
    }

    @Override
    protected void injectDependence() {

    }

    @OnClick({R.id.btnProfileBack, R.id.imvAvatar,
            R.id.btnChangName, R.id.btnChangePass, R.id.btnSignOut})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnProfileBack:
                finish();
                break;
            case R.id.imvAvatar:
                DialogUtils.showDialogGetPhotoMenu(this, this);
                break;
            case R.id.btnChangName:
                showDialogchangName();
                break;
            case R.id.btnChangePass:
                showDialogchangePass();
                break;
            case R.id.btnSignOut:
                sPref.edit().clear().commit();
                finish();
                Intent i = new Intent(this, LoginActivity.class);
                i.putExtra("email", "");
                i.putExtra("pass", "");
                i.putExtra("activity", "profile");
                startActivity(i);

                break;
        }
    }

    private void showDialogchangName() {
        new LovelyTextInputDialog(this, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.colorPrimary)
                .setTitle("Đổi tên")
                .setMessage("Hãy nhập tên tài khoản")
                .setIcon(R.drawable.ic_change)
                .setInputFilter("Phải khác tên cũ", new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        if(text.equals(name))
                            return false;
                        else return true;
                    }
                })
                .setInputFilter("Tên không thể trống", new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        if(text!=null){
                            return true;
                        }else return false;
                    }
                })
                .setConfirmButton("Lưu thay đổi", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        dialogUtils.showLoading();
                        newName=text;
                        presenter.replaceAccName(email,text);
                    }
                })
                .show();
    }

    private void showDialogchangePass() {
        Dialog passDialog = new Dialog(this);
        passDialog.setContentView(R.layout.dialog_changpass);
        EditText edtOldPass = passDialog.findViewById(R.id.oldPass);
        EditText edtNewPass = passDialog.findViewById(R.id.newPass);
        EditText edtRepeatNewPass = passDialog.findViewById(R.id.repeatNewPass);
        Button btnSave = passDialog.findViewById(R.id.btnOkChangPass);
        Button btnCancel = passDialog.findViewById(R.id.btnCancelChangPass);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = edtOldPass.getText().toString().trim();
                newPass = edtNewPass.getText().toString().trim();
                String repeatNewPass = edtRepeatNewPass.getText().toString().trim();
                if (!oldPass.equals("")) {
                    if (!newPass.equals("")) {
                        if (!repeatNewPass.equals("")) {
                            if (!newPass.equals(oldPass)) {
                                if (newPass.equals(repeatNewPass)) {
                                    showDialogConfirmChangePass(oldPass, newPass);
                                    passDialog.dismiss();
                                } else {
                                    edtRepeatNewPass.requestFocus();
                                    edtRepeatNewPass.setError("Mật khẩu không khớp");
                                }
                            } else {

                                edtNewPass.requestFocus();
                                edtNewPass.setError("Mật khẩu mới không được trùng");

                            }
                        } else {
                            edtRepeatNewPass.requestFocus();
                            edtRepeatNewPass.setError("Hãy nhập lại mật khẩu mới");
                        }
                    } else {
                        edtNewPass.requestFocus();
                        edtNewPass.setError("Hãy nhập  mật khẩu mới");
                    }
                } else {
                    edtOldPass.requestFocus();
                    edtOldPass.setError("Hãy nhập  mật khẩu cũ");
                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passDialog.dismiss();
            }
        });
        passDialog.show();
    }

    private void showDialogConfirmChangePass(String oldPass, String newPass) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("Mật khẩu của bạn sẽ được thay đổi?")
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogUtils.showLoading();
                        presenter.resetPassword(email, oldPass, newPass);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onFail(String s) {
        dialogUtils.hideLoading();
        DialogUtils.showInfor(this, s);

    }

    @Override
    public void onUpdatePassSuccess(String result) {
        sPref.edit().putString("pass", newPass).commit();
        dialogUtils.hideLoading();
        DialogUtils.showInfor(this, result);
    }

    @Override
    public void onupdateNameSuccess(String result) {
        sPref.edit().putString("name", newName).commit();
        tvAccName.setText(newName);
        dialogUtils.hideLoading();
        DialogUtils.showInfor(this, result);
    }

    @Override
    public void onUploadPicSuccess(String avatarUrl) {
        presenter.updateAvatar(email,avatarUrl);
        newAvatar=avatarUrl;
    }

    @Override
    public void onUpdateAvatarSuccess(String result) {
        sPref.edit().putString("avatar", newAvatar).commit();
        Glide.with(this).load(newAvatar).error(getResources()
                .getDrawable(R.drawable.ic_no_image)).into(imvAvatar);
        dialogUtils.hideLoading();
        DialogUtils.showInfor(this, result);

    }

    @Override
    public void choossePhoto() {
        if(isReadStorageAllowed()){
            startActivityForResult(new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    , Constants.REQUEST_CHOOSE_PHOTO);
        }else{
            requestStoragePermission();
        }

    }

    @Override
    public void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, Constants.REQUEST_TAKE_PHOTO);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_TAKE_PHOTO) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
        } else if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CHOOSE_PHOTO && data != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bitmap != null) {
            ByteArrayOutputStream imageByte = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, imageByte);
            new AlertDialog.Builder(this)
                    .setMessage("Avatar của bạn sẽ được thay đổi?")
                    .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialogUtils.showLoading();
                            presenter.updateAvatar(imageByte.toByteArray());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        }


    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
        }
        ActivityCompat.requestPermissions(this,new String[]
                {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},Constants.REQUEST_WRITE_STORAGE);
    }

    private boolean isReadStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_WRITE_STORAGE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        , Constants.REQUEST_CHOOSE_PHOTO);
                Toast.makeText(this,
                        "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this,
                        "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}
