package com.example.cloudmusic.response.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    /**
     * 使用单例模式
     */
    private PermissionManager() {}
    private static PermissionManager permissionManager;
    public static PermissionManager getInstance(){
        if(permissionManager ==null){
            synchronized (PermissionManager.class){
                if(null== permissionManager){
                    permissionManager =new PermissionManager();
                }
            }
        }
        return permissionManager;
    }

    /**
     * 申请权限，
     * @param stringPermission 要检查的权限
     * @param requestCode 请求码
     * @return 是否已同意
     */
    public boolean checkPermission(Activity activity, Context context, String stringPermission, int requestCode) {
        boolean flag = false;
        //已有权限
        if (ContextCompat.checkSelfPermission(context, stringPermission) == PackageManager.PERMISSION_GRANTED) {
            flag = true;
        } else {
            //申请权限
            ActivityCompat.requestPermissions(activity, new String[]{stringPermission}, requestCode);
        }
        return flag;
    }
}
