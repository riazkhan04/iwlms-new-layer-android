package com.orsac.android.pccfwildlife.MyUtils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

public class PermissionHelperClass {
    Context context;
    Activity activity;
    int Location_REQUEST_CODE=101,Camera_REQUEST_CODE=104,File_REQUEST_CODE=105;

    public PermissionHelperClass(Context context) {
        this.context = context;
    }

    public PermissionHelperClass(Activity activity) {
        this.activity = activity;
    }

    public PermissionHelperClass(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public boolean hasLocationPermission(){
        //Permission methods
            int res = 0;
            //string array of permissions,
            String[] permissions = new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION};

            for (String perms : permissions) {
                res = activity.checkCallingOrSelfPermission(perms);
                if (!(res == PackageManager.PERMISSION_GRANTED)) {
                    return false;
                }
            }
            return true;
        }

    public void requestLocationPermission() {
        String[] permissions = new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, Location_REQUEST_CODE);
        }
    }

    public boolean hasCameraNStoragePermission(){
        //Permission methods
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        for (String perms : permissions) {
            res = activity.checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    public void requestCameraNStoragePermission() {
        String[] permissions = new String[]{
                android.Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, Camera_REQUEST_CODE);
        }
    }

    public boolean hasStoragePermission(){
        //Permission methods
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        for (String perms : permissions) {
            res = activity.checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    public void requestStoragePermission(String type) {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, File_REQUEST_CODE);
        }
    }

}
