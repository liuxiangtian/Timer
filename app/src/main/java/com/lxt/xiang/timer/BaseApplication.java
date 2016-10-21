package com.lxt.xiang.timer;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


public class BaseApplication extends Application {

    private static BaseApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static BaseApplication getINSTANCE() {
        return INSTANCE;
    }

}
