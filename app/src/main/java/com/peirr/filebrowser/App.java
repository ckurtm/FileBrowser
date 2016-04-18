package com.peirr.filebrowser;

import android.app.Application;

import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.DataCastManager;

/**
 * Created by kurt on 2016/02/23.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CastConfiguration options =new CastConfiguration.Builder("84B70D9D")
                .enableAutoReconnect()
                .enableDebug()
                .build();
        DataCastManager.initialize(this,options);
    }
}
