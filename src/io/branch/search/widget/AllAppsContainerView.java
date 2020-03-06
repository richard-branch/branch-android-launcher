package io.branch.search.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;

import com.android.launcher3.R;

import java.util.Map;

import io.branch.search.widget.app.BranchSearchFragment;

public class AllAppsContainerView extends RelativeLayout implements DragSource {
    private BranchSearchFragment branchSearchFragment;

    public AllAppsContainerView(Context context) {
        this(context, null);
    }

    public AllAppsContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.branch_apps_drawer, null);
        addView(view);

        AppCompatActivity activity = (AppCompatActivity) getContext();

        branchSearchFragment = new BranchSearchFragment();
        branchSearchFragment.setEventLogger(new IBranchEventLogger() {
            @Override
            public void logBranchEvent(@NonNull String name,
                                       @NonNull Map<String, String> data) {
                // Do nothing.
            }

            @Override
            public void logDebugEvent(@NonNull String tag,
                                      int level,
                                      @NonNull String message,
                                      @Nullable Throwable throwable) {
                switch (level) {
                    case Log.DEBUG: Log.d(tag, message, throwable); break;
                    case Log.VERBOSE: Log.v(tag, message, throwable); break;
                    case Log.INFO: Log.i(tag, message, throwable); break;
                    case Log.WARN: Log.w(tag, message, throwable); break;
                    case Log.ERROR: Log.e(tag, message, throwable); break;
                }
            }
        });
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, branchSearchFragment)
                .commit();
    }

    public void notifyOpen(boolean open) {
        branchSearchFragment.notifyOpen(open);
    }

    public boolean isScrolled() {
        return branchSearchFragment.canScrollUp();
    }

// empty functions from interfaces
    public void onDropCompleted(View target, DropTarget.DragObject d, boolean success) {
    }

    @Override
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
    }

    public void reset(boolean animate) {
        branchSearchFragment.updateQuery("");
    }

// empty functions to remove copile errors
    public void onScrollUpEnd() {
    }

    public void addSpringFromFlingUpdateListener(ValueAnimator animator, float velocity) {
    }

    public void setLastSearchQuery(String query) {
    }

    public void onClearSearchResult() {
    }

    public void onSearchResultsChanged() {
    }
}
