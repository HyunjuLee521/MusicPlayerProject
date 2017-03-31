package com.hj.user.musicplayerproject;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by USER on 2017-03-23.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
