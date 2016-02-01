package com.xltech.client.service;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by JooLiu on 2016/1/25.
 */
public class ManActivitys {
    private static Stack<Activity> activityStack;
    private static ManActivitys instance = null;

    public static ManActivitys getInstance() {
        if (instance == null) {
            instance = new ManActivitys();
            if (activityStack == null) {
                activityStack = new Stack<Activity>();
            }
        }

        return instance;
    }

    public void popActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    public int getActivitySize() {
        return activityStack.size();
    }

    public Activity currentActivity() {
        Activity activity;
        try {
            activity = activityStack.lastElement();
        } catch (Exception e) {
            return null;
        }
        return activity;
    }

    public void pushActivity(Activity activity) {
        activityStack.add(activity);
    }

    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            } else {
                popActivity(activity);
            }
        }
    }

    public void popAllActivityExceptOne() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }
}
