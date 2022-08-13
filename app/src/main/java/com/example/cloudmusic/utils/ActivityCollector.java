package com.example.cloudmusic.utils;

import android.app.Activity;

import com.example.cloudmusic.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishActivity(Class<? extends BaseActivity> activityClass){
        for (Activity activity : activities) {
            if (activity.getClass().equals(activityClass)) {
                activity.finish();
                activities.remove(activity);
                break;
            }
        }
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }
}
