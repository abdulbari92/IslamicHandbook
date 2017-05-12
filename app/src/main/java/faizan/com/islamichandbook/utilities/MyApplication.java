package faizan.com.islamichandbook.utilities;

import android.app.Application;
import android.content.Context;

/**
 * Created by buste on 4/16/2017.
 */

public class MyApplication extends Application {
    public static MyApplication myInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        myInstance = this;
    }

    public static Context getAppContext() {
        return myInstance.getApplicationContext();
    }

    public static MyApplication getMyInstance() {
        return myInstance;
    }
}