# AppDrawer+ SDK Integration Guide

## Overview

Branch AppDrawer+ blends the power of Branch in-app search technology with the familiarity of an app drawer. 

Our AppDrawer+ SDK (supported on Android 6 to 10) makes integrating AppDrawer+ into your Launcher easy. This guide will walk you through the steps to replace your Launcher's current app drawer with AppDrawer+. 

For your reference, using the steps below, we have created and shared an example integration of the AppDrawer+ SDK into the AOSP Android 10 Launcher3 launcher. It may be useful to look at the code diff in the commit history to see the exact changes made from the original AOSP code. You are welcome to use any of this code for your own integration. [branch-android-launcher on Github](https://github.com/BranchMetrics/branch-android-launcher) (to build and run without errors related to use of system APIs, choose the `aospWithoutQuickstep` build variant).

> Note: This guide assumes that your launcher is based on the AOSP (Android Open Source) [Launcher3 launcher](https://android.googlesource.com/platform/packages/apps/Launcher3/+/refs/heads/master). If your launcher is not based on Launcher3, or you would like to integrate the AppDrawer+ SDK into a product that is not a launcher, please reach out to us for additional assistance.

## Getting Started

### Import the library

1. Add the library to your project using one of the following options:
    - Option A - Import the compiled AAR file:
        1. Paste the AAR file in your application module, in the libs subdirectory
        2. In your app build.gradle file, add the libs folder as a flatDir repository:

            ```java
            repositories {
             flatDir { dirs 'libs' }
            }
            ```

        3. In your app build.gradle file, add the SDK as a dependency. For a file
        named branchuisdk-release.aar, add:

            ```java
            implementation(name: 'branchuisdk-release', ext: 'aar')
            ```

    - Option B - Add the compiled AAR file as a module:
        1. Click File > New > New Module.
        2. Click Import .AAR Package then click Next.
        3. Enter the location of the compiled AAR or JAR file then click Finish.
2. In both cases, you should add the AAR dependencies as dependencies of your own
project:

    ```java
    // Android
    implementation 'com.android.support:design:28.0.0'
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support:recyclerview-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    implementation 'android.arch.lifecycle:extensions:1.1.1'
    implementation 'com.google.android.gms:play-services-location:16.0.0'
    implementation 'androidx.room:room-runtime:2.2.0'
    annotationProcessor 'androidx.room:room-compiler:2.2.0'
    // Other utilities
    implementation 'io.branch.sdk.android:search:1.5.1'
    implementation 'com.facebook.fresco:fresco:1.12.1'
    implementation 'com.squareup.okhttp3:okhttp:3.12.6'
    ```

### Add Manifest Entries

1. Replace <YOUR_BRANCH_KEY> with your Branch Discovery SDK Key in the
AndroidManifest:

    ```java
    <meta-data
     android:name="io.branch.sdk.BranchKey"
     android:value="<YOUR_BRANCH_KEY>" />
    ```

2. For the opt-in flow, declare the BranchOptInActivity activity as follows:

    ```java
    <activity
     android:name="io.branch.search.widget.optin.BranchOptInActivity"
     android:theme="@style/BranchOptIn.Theme" />
    ```

## Code Integration

1. Initial steps to integrating the SDK with the launcher
    - Change the application theme (typically called `BaseLauncherTheme`) in
    `styles.xml` to inherit from `BranchApp.Theme`. Note, it is likely `BaseLauncherTheme` is defined in multiple `styles.xml` files for different API levels/device types. This change has to be applied to all `BaseLauncherTheme` definitions.
    - In `src/com/android/launcher3/BaseActivity.java`, change the base class
    from `android.app.Activity` to `androidx.appcompat.app.AppCompatActivity`
2. Replace the app drawer view with the AppDrawer+ View
    - Create a new apps container view
    (fully-qualified name = `io.branch.search.widget.AllAppsContainerView.java`). Make sure this new view extends `RelativeLayout` and implements the `DragSource` interface (this will help eliminate compile errors more easily). You can can keep the override methods empty.
    - Create a new layout resource (`/res/layout/branch_apps_drawer.xml`)

        ```java
        <io.branch.search.widget.AllAppsContainerView
            xmlns:android="http://schemas.android.com/apk/res/android"
            android:layout_width="match_parent"
            android:layout_height="match_parent">
            <FrameLayout
                android:id="@+id/fragment_container"
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>
        </io.branch.search.widget.AllAppsContainerView>
        ```

    - Find all references to the app drawer layout resource used by the launcher, `res/layout/all_apps.xml`, and replace them with references to our newly created resource, `/res/layout/branch_apps_drawer.xml`.
    - Copy all method definitions from `com.android.launcher3.allapps.AllAppsContainerView` and add them to `[io.branch.search.widget.AllAppsContainerView.java](http://io.branch.search.widget.AllAppsContainerView.java)` as empty methods. These will be unused methods called by the launcher.
        1. If method returns an object, return `null`
        2. If method returns boolean, return `false`
    - Replace the original `com.android.launcher3.allapps.AllAppsContainerView` with
    `io.branch.search.widget.AllAppsContainerView` in all the source files.
        1. The easiest way to do this depends on the IDE you are using. Some IDEs, like Android Studio, will have an equivalent of "find all usages" (of a given class like `com.android.launcher3.allapps.AllAppsContainerView`). Otherwise, you may need to comment out all code in the original `AllAppsContainerView` (including class definition) and go trough individual lint/compile errors while importing the new view.
    - Comment out all usages on the original `AllAppsContainerView`'s methods. This can be done via the IDEs "find all usages" feature or temporarily removing a given method, then attempting to compile and commenting out the line of code that throws the compile error.
        - Mark or otherwise keep track of all the lines of code that you commented out in the previous step. Some of the methods will need to be re-implemented in our new view and and invoked from Launcher like before.
3. Implement the middleware between the launcher and the Branch UI. `AllAppsContainerView` should contain all the code required for the launcher and the Branch UI to interact with one another.
    - In the constructor of `AllAppsContainerView`, add the `BranchSearchFragment`:

        ```java
        branchSearchFragment = new BranchSearchFragment();
         activity.getSupportFragmentManager().beginTransaction()
         .replace(R.id.fragment_container, branchSearchFragment)
         .commit();
        ```

    - Implement the following methods. Note that the given examples are bare bone implementations that you may want/need to tweak (in order to account for nullability, illegal state, etc).
        1. `notifyOpen(boolean open)`
            - AppDrawer+ needs to know when it’s fully shown or not. It should be marked as open when the animation progress is 0, and not open when it is greater
            than 0. This can be invoked, for example, in `AllAppsTransitionController.setProgress()`
            - Example implementation

                ```java
                public void notifyOpen(boolean open) {
                    branchSearchFragment.notifyOpen(open);
                }
                ```

        2. `resetToZeroState()`
            - AppDrawer+ needs to clear the drawer query when the drawer is being hidden away. This can be done, for example, in the original `AllAppsContainerView`'s  method `reset(boolean animate)` (in which case you should make the usages of this method active again).
            - Example implementation

                ```java
                public void resetToZeroState() {
                    branchSearchFragment.updateQuery("");
                }
                ```

        3. `boolean canScrollUp()`
            - Launcher needs to know whether the user's swipe event is meant to result in scrolling through apps or opening the app drawer. The original `AllAppsContainerView` answered this question via `shouldContainerScroll(MotionEvent ev)`, our new view will answer this question via `canScrollUp()`. Thus the simplest way to implement this would be to make usages of `shouldContainerScroll` active again and replace the dummy `return false` with  `return canScrollUp()`
            - Example implementation

                ```java
                public boolean canScrollUp() {
                    return branchSearchFragment.canScrollUp();
                }
                ```

    - In the activity that contains `BranchSearchFragment`, ensure that `onActivityResult()`
    calls `super.onActivityResult(requestCode, resultCode, data);`
4. Add finishing touches
    - Add the code to reset the Branch UI (`resetToZeroState`) when opening/closing the drawer (see `AllAppsTransitionController.onProgressAnimationEnd`).
    - `AllAppsTransitionController.setProgress` and `AllAppsTransitionController.setStateWithAnimation` can be used for functionality that depends on the vertical movement of the app drawer
        - fade-in/fade-out, showing/hiding the keyboard, showing/hiding the
        fragment
