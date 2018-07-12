package com.ly.rxjavasample_1x;

import android.app.Application;

/**
 * Create by LiuYang on 2018/7/12 09:15
 */
public class App extends Application {
    private static App INSTANCE;

    public static App getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }
}
