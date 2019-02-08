package com.google.codelabs.mdc.java.shrine.application;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.parse.Parse;

public class ShrineApplication extends Application {
    private static ShrineApplication instance;
    private static Context appContext;

    public static ShrineApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context mAppContext) {
        this.appContext = mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("j9A9UOZTzG4BRYYNb5JcGR5De3pCOQbA8OCMZcQg")
                .clientKey("0Oa3us8Q0EZulKZuaMN5Vlh1StBDAW1PDcbuVeWP")
                .server("https://parseapi.back4app.com")
                .build()
        );
        instance = this;

        this.setAppContext(getApplicationContext());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}