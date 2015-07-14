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
    UsageStats currentForegndPackage;
    Date startTime = new Date();
    Date endTime = new Date();

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
        // We get usage stats for the last 10 seconds
        List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000*10, time);
        // Sort the stats by the last time used
        if(stats != null) {
            SortedMap<Long,UsageStats> mySortedMap = new TreeMap<>();
            for (UsageStats usageStats : stats) {
                mySortedMap.put(usageStats.getLastTimeUsed(),usageStats);
            }

            if(mySortedMap != null && !mySortedMap.isEmpty()) {
                currentForegndPackage =  mySortedMap.get(mySortedMap.lastKey());
                Log.v(TAG, currentForegndPackage.getPackageName());
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mUsageStatsManager = (UsageStatsManager) getApplicationContext()
                .getSystemService("usagestats"); //Context.USAGE_STATS_SERVICE
        //startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
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
