package com.congp.finkids.ui.main;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.congp.finkids.R;
import com.congp.finkids.common.Constants;
import com.congp.finkids.common.DialogUtils;
import com.congp.finkids.data.User;
import com.congp.finkids.service.GoogleService;
import com.congp.finkids.ui.profile.ProfileActivity;
import com.congp.finkids.ui.profile.ProfileView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, MainView {
    private ProgressDialog dialog;
    private DialogUtils dialogUtils;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Double latitude, longitude;
    Geocoder geocoder;
    @BindView(R.id.fAddCricle)
    FloatingActionButton fAddCricle;
    @BindView(R.id.fProfile)
    FloatingActionButton fProfile;
    @BindView(R.id.fabMenu)
    FloatingActionMenu fabMenu;
    private GoogleMap mMap;
    private Marker mCurrLocationMarker;
    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkLocationPermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();
        dialogUtils = new DialogUtils(dialog, this);
        startLocationService();
        String email=getSharedPreferences(Constants.SPF_NAME, Context.MODE_PRIVATE).getString("email","");
        presenter = new MainPresenter(this,email);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));

            List<Address> addresses = null;
            String stateName = "";
            String cityName = "";
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                cityName = addresses.get(0).getAddressLine(0);
                stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            LatLng latLng = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(cityName);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));

    }

    @Override
    protected void onPause() {
        super.onPause();
        registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
//        unregisterReceiver(broadcastReceiver);
    }


    private boolean checkLocationPermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION))) {


            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION

                        },
                        Constants.REQUEST_PERMISSIONS);

            }
            return false;
        } else {
            return true;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Constants.REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng cho phép  bật GPS để sử dụng ứng dụng", Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    private void startLocationService() {
        if (checkLocationPermission()) {
            if (mPref.getString("service", "").matches("")) {
                medit.putString("service", "service").commit();

                Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                startService(intent);

            } else {
                Toast.makeText(getApplicationContext(), "Service is already running", Toast.LENGTH_SHORT).show();
                stopLocationService();
                startLocationService();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please enable the gps", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService() {
        if (mPref.getString("service", "").matches("")) {

        } else {
            medit.putString("service", "").commit();
            Intent intent = new Intent(getApplicationContext(), GoogleService.class);
            stopService(intent);
        }
    }

    @OnClick({R.id.fAddCricle, R.id.fProfile, R.id.fabMenu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fAddCricle:
                showDialogAddMemberCircle();
                fabMenu.close(true);
                break;
            case R.id.fProfile:
                startActivity(new Intent(this, ProfileActivity.class));
                fabMenu.close(true);
                break;
            case R.id.fabMenu:
                if (fabMenu.isOpened()) {
                    fabMenu.close(true);
                }
                break;
        }
    }

    private void showDialogAddMemberCircle() {
        new LovelyTextInputDialog(this, R.style.EditTextTintTheme)
                .setTitle("Thêm thành viên")
                .setMessage("Vui lòng nhập email thành viên")
                .setIcon(R.drawable.ic_add)
                .setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .setInputFilter("Email không đúng", new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        Pattern VALID_EMAIL_ADDRESS_REGEX =
                                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(text);
                        return matcher.find();
                    }
                })
                .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {
                        dialogUtils.showLoading();
                        findIDEmail(text);
                    }
                })
                .show();
    }

    private void findIDEmail(String text) {
        presenter.findUserEmail(text);
    }

    @Override
    public void onFindUserSuccess(User user) {
        dialogUtils.hideLoading();
        showDialogConfrim(user);


    }

    private void showDialogConfrim(User user) {
        Dialog passDialog = new Dialog(this);
        passDialog.setContentView(R.layout.friend_infor);
        ImageView imvAvatar = passDialog.findViewById(R.id.imvAvatar);
        Glide.with(this).load(user.getAvatar()).error(getResources().getDrawable(R.drawable.ic_no_image)).into(imvAvatar);
        TextView tvAccEmail = passDialog.findViewById(R.id.tvAccEmail);
        tvAccEmail.setText(user.getEmail());
        TextView tvAccName = passDialog.findViewById(R.id.tvAccName);
        tvAccName.setText(user.getName());
        Button btnSave = passDialog.findViewById(R.id.btn_add_member);
        Button btnCancel = passDialog.findViewById(R.id.btn_cancel);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passDialog.dismiss();
                dialogUtils.showLoading();
                presenter.addMemberToCricle(user.getEmail());
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

    @Override
    public void onRequestFail(String s) {
        dialogUtils.hideLoading();
        DialogUtils.showInfor(this,s);

    }

    @Override
    public void onAddMemberToCricleSussess(String result) {
        dialogUtils.hideLoading();
        DialogUtils.showInfor(this,result);
    }
}
