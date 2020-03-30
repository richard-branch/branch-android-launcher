# branch-android-launcher Integration Guide

## Overview

Branch AppDrawer+ blends the power of Branch in-app search technology with the familiarity of an app drawer.

Our AppDrawer+ SDK (supported on Android 6 to 10) makes integrating AppDrawer+ into your Launcher easy. This guide will walk you through the steps to replace your Launcher's current app drawer with AppDrawer+. 

For your reference, using the steps below, we have created and shared an example integration of the AppDrawer+ SDK into the AOSP Android Pie (9.0) Launcher3 launcher. It may be useful to look at the code diff in the commit history to see the exact changes made from the original AOSP code. You are welcome to use any of this code for your own integration. [branch-android-launcher on Github](https://github.com/BranchMetrics/branch-android-launcher)

*Note: This guide assumes that your launcher is based on the AOSP (Android Open Source)  Launcher3 launcher. If your launcher is not based on Launcher3, or you would like to integrate the AppDrawer+ SDK into a product that is not a launcher, please reach out to us for additional assistance.*

## Getting Started

**Import the library**
<ol>
<li>Add the library to your project using one of the following options:</li>

<ol><li>Option A - Import the compiled AAR file:</li>
<ol>
<li>Paste the AAR file in your application module, in the libs subdirectory </li>
<li>In your app build.gradle file, add the libs folder as a flatDir repository:

```
repositories {
   flatDir { dirs 'libs' }
}
```
</li>

<li>In your app build.gradle file, add the SDK as a dependency. For a file named branchuisdk-release.aar, add:

```implementation(name: 'branchuisdk-release', ext: 'aar')```</li>
</ol>


<li>Option B - Add the compiled AAR file as a module: </li>
<ol>
<li>Click File > New > New Module. </li>
<li>Click Import .AAR Package then click Next.</li>
<li>Enter the location of the compiled AAR or JAR file then click Finish.</li>
</ol></ol>

<li>In both cases, you should add the AAR dependencies as dependencies of your own project:</li>

```
// Android
implementation 'com.android.support:design:28.0.0'
implementation 'com.android.support:appcompat-v7:28.0.0'
implementation 'com.android.support:recyclerview-v7:28.0.0'
implementation 'com.android.support.constraint:constraint-layout:1.1.3'
implementation 'android.arch.lifecycle:extensions:1.1.1'
implementation 'com.google.android.gms:play-services-location:16.0.0'
implementation 'androidx.room:room-runtime:2.2.0'
annotationProcessor 'androidx.room:room-compiler:2.2.0'
```
```
// Other utilities
implementation 'io.branch.sdk.android:search:1.5.1'
implementation 'com.facebook.fresco:fresco:1.12.1'
implementation 'com.squareup.okhttp3:okhttp:3.12.6'
```
</ol>

**Add Manifest Entries**
<ol>
<li>Replace <YOUR_BRANCH_KEY> with your Branch Discovery SDK Key in the AndroidManifest:

```
<meta-data
  android:name="io.branch.sdk.BranchKey"
  android:value="<YOUR_BRANCH_KEY>" />
```
</li>


<li>For the opt-in flow, declare the BranchOptInActivity activity as follows:

```
<activity
   android:name="io.branch.search.widget.optin.BranchOptInActivity"
   android:theme="@style/BranchOptIn.Theme" />
   ```
   </li></ol>
   
   **Optional but recommended: preload the UI**
<ol><li>In your application’s onCreate() method, call BranchSearchController.preload(this). This will speed up results during the first display of the UI.</li></ol>


## Code Integration

1. Initial steps to integrating the SDK with the launcher 

    1. Change the application theme (typically called BaseLauncherTheme) in **styles.xml** to inherit from BranchApp.Theme

    2. In **src/com/android/launcher3/BaseActivity.java**, change the base class from android.app.Activity to android.support.v7.app.AppCompatActivity

2. Replace the app drawer view with the AppDrawer+ View

    1. Create a new apps container view (**src/io/branch/search/widget/AllAppsContainerView.java**) that implements the DragSource interface 

        1. This will help eliminate compile errors easily

    2. Create an empty (frame) layout to use as a container (**/res/layout/branch_apps_drawer.xml**)

    3. Change the XML resource used by the launcher to use our newly created AllAppsContainerView (**res/layout/all_apps.xml**)

    4.  Add empty methods to the AllAppsContainerView

        1. These are unused methods called by the launcher

    5. Replace the original com.android.launcher3.allapps.AllAppsContainerView with io.branch.search.widget.AllAppsContainerView in all the source files

        1. The easiest way to accomplish this is to comment out all the code in com.android.launcher3.allapps.AllAppsContainerView (including the class definition) and go through the errors while importing the new view

3. Resolve additional compile errors

    1. Comment out the lines of code that are still not compiling

        1. Compile errors will occur since our new view does not have the methods of the old view

    2. Mark all the lines of code that are commented so they can be easily found

        1. Some of the methods will need to be re-implemented in our new view

4. Implement the middleware between the launcher and the Branch UI 

 

    1. AllAppsContainerView contains all the code required for the launcher and the Branch UI to interact with one another. 

    2. Add the BranchSearchFragment to the view 

        ```
        branchSearchFragment = new BranchSearchFragment();

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, branchSearchFragment)
                .commit();```
    3. Inside the View, implement methods:

        1. AppDrawer+ needs to know when it’s fully shown or not with ```BranchSearchFragment.notifyOpen(boolean)```. It should be marked as open when the animation progress is 0, and not open when it is greater than 0. This can be done, for example, in ```AllAppsTransitionController.setProgress()```

        2. When resetting the state after hiding, you should clear the drawer query with ```BranchSearchFragment.updateQuery("")```. This can be done, for example, in ```AllAppsContainerView.reset()```

        3. ```AllAppsContainerView.isScrolled()``` should be implemented to know whether the UI was scrolled down. We provide a utility called ```BranchSearchFragment.canScrollUp()```.

    4. Depending on the launcher used, other methods will be required to interact with the launcher 

    5. In the activity that contains BranchSearchFragment, ensure that onActivityResult calls ```super.onActivityResult(requestCode, resultCode, data); ```

        1. Include the line above if it is not called 

5. Add finishing touches

    1. Add the code to reset the Branch UI when opening/closing the drawer to the onProgressAnimationEnd method in **src/com/android/launcher3/allapps/AllAppsTransitionController.java**

        1. This is required to always reset the AppDrawer+ to zero state and open/close the keyboard as needed

    2. setProgress and setStateWithAnimation can be used for functionality that depends on the vertical movement of the app drawer 

        1. fade-in/fade-out, showing/hiding the keyboard, showing/hiding the fragment

    3. In the canInterceptTouch in **src_ui_overrides/com/android/launcher3/uioverrides/AllAppsSwipeController.java**, change the last condition to use the "isScrolled" method of AllAppsContainerView

        1. This controls when the vertical swipe should scroll the app drawer and when it should open the app drawer
        
        
