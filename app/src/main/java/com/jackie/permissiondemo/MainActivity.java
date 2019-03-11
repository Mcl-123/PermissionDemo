package com.jackie.permissiondemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final int REQUEST_PERMISSION_SETTING = 2;
    private final String SHOULD_SHOW_CUSTOM_LOCATION_PERMISSION_DIALOG = "SHOULD_SHOW_CUSTOM_LOCATION_PERMISSION_DIALOG";
    private String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private boolean shouldShowCustomPermissionDialog = false;

    SharedPreferences sp;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);


        sp = getSharedPreferences("sp", Context.MODE_PRIVATE);

        button.setOnClickListener(v -> {
            if (checkPermission()) {
                // do something
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // shouldShowRequestPermissionRationale 只有在拒绝并且未点击以后不再显示的按钮时，才会返回true；
                // 默认情况及拒绝并且点击以后不再显示的按钮时， 返回false
            } else if (!shouldShowRequestPermissionRationale(locationPermission)) {
                if (shouldShowCustomPermissionDialog) {
                    showPermissionMessageDialog();
                } else {
                    shouldShowCustomPermissionDialog = true;
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putBoolean(SHOULD_SHOW_CUSTOM_LOCATION_PERMISSION_DIALOG, true).commit();
                }
            }
        }
    }

    private boolean checkPermission() {
        shouldShowCustomPermissionDialog = sp.getBoolean(SHOULD_SHOW_CUSTOM_LOCATION_PERMISSION_DIALOG, false);
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(this, locationPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{locationPermission}, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
    }

    private void showPermissionMessageDialog() {
        new AlertDialog.Builder(this)
                .setMessage("获取蓝牙列表需要开启位置权限, 请在设置中打开位置权限")
                .setPositiveButton("好的", (d, i) -> {
                    goPermissionSetting();
                })
                .setNegativeButton("不要", null)
                .show();
    }

    private void goPermissionSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }
}
