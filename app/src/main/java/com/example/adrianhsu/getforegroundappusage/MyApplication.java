package com.example.adrianhsu.getforegroundappusage;

/**
 * Created by AdrianHsu on 15/7/14.
 */
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = MyApplication.class.getSimpleName();
    private Handler handler;
    private Runnable runDetect = new Runnable() {
        @Override
        public void run() {
            //detect();
            handler.postDelayed(this, 1000);
            Log.d( TAG ,"LOL");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);
        handler = new Handler(getMainLooper());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        handler.removeCallbacks(runDetect);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        handler.removeCallbacks(runDetect);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        handler.removeCallbacks(runDetect);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        handler.postDelayed(runDetect, 1000);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
