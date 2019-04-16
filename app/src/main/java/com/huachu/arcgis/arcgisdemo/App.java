package com.huachu.arcgis.arcgisdemo;

import android.app.Application;



import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ArcGISRuntimeEnvironment.setLicense(getString(R.string.license));

    }


}
