package com.sumon.map.helper;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.binjar.prefsdroid.Preference;

/**
 * Created by Jannatul Fardous on 6/5/2017.
 */

public class MapsUserInfo extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Preference.load().using(this).prepare();
        MultiDex.install(this);
    }
}
