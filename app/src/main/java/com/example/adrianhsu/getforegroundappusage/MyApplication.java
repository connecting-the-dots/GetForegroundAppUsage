package com.example.adrianhsu.getforegroundappusage;

/**
 * Created by AdrianHsu on 15/7/14.
 */
import android.app.Activity;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {


    UsageStatsManager mUsageStatsManager;
    static UsageStats currentForegndPackage;
    static UsageStats tempForegndPackage = null;
    static long startTime;
    static long endTime;

    private static final String TAG = MyApplication.class.getSimpleName();
    private Handler handler;

    private Runnable runDetect = new Runnable() {
        @Override
        public void run() {

            handler.postDelayed(this, 1000);

            checkForegroundApp();
        }
    };

    public void checkForegroundApp() {

        long time = System.currentTimeMillis();
        // We get usage stats for the last 1000 seconds

        mUsageStatsManager = (UsageStatsManager) getApplicationContext()
                .getSystemService("usagestats"); //Context.USAGE_STATS_SERVICE

        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*1000, time);
        // Sort the stats by the last time used
        if(stats != null) {
            SortedMap<Long,UsageStats> mySortedMap = new TreeMap<>();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
            }

            if(mySortedMap != null && !mySortedMap.isEmpty()) {
                if(mySortedMap.get(mySortedMap.lastKey()).getPackageName().contentEquals( "com.android.systemui" )
                        || mySortedMap.get(mySortedMap.lastKey()).getPackageName().contentEquals( "com.asus.launcher" )) {
                    return;
                }
                currentForegndPackage =  mySortedMap.get(mySortedMap.lastKey());
                if(tempForegndPackage == null ) { //first time
                    startTime = System.currentTimeMillis();
                    tempForegndPackage = currentForegndPackage;
                }
                else {
                    Log.v(TAG, "Current Foreground App: " + currentForegndPackage.getPackageName());
                    Log.v(TAG, "Temp Foreground App: " + tempForegndPackage.getPackageName());

                    if (currentForegndPackage.getPackageName().contentEquals(tempForegndPackage.getPackageName())) {

                        tempForegndPackage = currentForegndPackage;
                    } else {
                        endTime = tempForegndPackage.getLastTimeUsed();
                        Log.v(TAG, "Start time: " + startTime);
                        Log.v(TAG, "End time: " + endTime);
                        Log.v(TAG, "Desired interval: " + ((endTime - startTime) / 1000) + " sec");
                        startTime = System.currentTimeMillis();
                        endTime = 0;
                        tempForegndPackage = currentForegndPackage;
                    }
                }
            }

        }
    }

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
