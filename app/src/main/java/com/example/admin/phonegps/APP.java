package com.example.admin.phonegps;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by admin on 2017/9/17.
 */

public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化百度地图SDK
        SDKInitializer.initialize(getApplicationContext());

    }
}
