package com.android.launcher3;

import android.app.Application;

import io.branch.search.widget.BranchSearchController;

public class LauncherApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BranchSearchController.preload(this);
    }
}
