package com.app.cinx;

import android.app.Application;

public class CinxApp extends Application {
    private static CinxApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static CinxApp getInstance() {
        return instance;
    }
}

