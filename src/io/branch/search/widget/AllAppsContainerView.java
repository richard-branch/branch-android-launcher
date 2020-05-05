package io.branch.search.widget;

import android.animation.ValueAnimator;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.allapps.AllAppsRecyclerView;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.allapps.FloatingHeaderView;
import com.android.launcher3.allapps.SearchUiManager;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.views.RecyclerViewFastScroller;

import io.branch.search.widget.app.BranchSearchFragment;

public class AllAppsContainerView extends RelativeLayout implements DragSource {
    private Launcher mLauncher;
    private BranchSearchFragment branchSearchFragment;

    public AllAppsContainerView(Context context) {
        this(context, null);
    }

    public AllAppsContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllAppsContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mLauncher = Launcher.getLauncher(context);
        branchSearchFragment = new BranchSearchFragment();
        mLauncher.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, branchSearchFragment)
                .commit();
    }

    public void notifyOpen(boolean open) {
        if (branchSearchFragment != null && branchSearchFragment.getActivity() != null && branchSearchFragment.isAdded()) {
            branchSearchFragment.notifyOpen(open);
        }
    }

    /**
     * Original AllAppsContainerView's method
     */
    public void onDropCompleted(View target, DropTarget.DragObject d, boolean success) { }
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) { }
    public AllAppsRecyclerView getActiveRecyclerView() {
        return null;
    }
    public class AdapterHolder {
        public static final int MAIN = 0;
        public static final int WORK = 1;
        public AllAppsRecyclerView recyclerView;
        AdapterHolder(boolean isWork) { }
        void setup(@NonNull View rv, @Nullable ItemInfoMatcher matcher) { }
        void applyPadding() { }
        public void applyVerticalFadingEdgeEnabled(boolean enabled) { }
    }

    public MultiValueAlpha.AlphaProperty getAlphaProperty(int index) {
        return null;
    }
    public AllAppsStore getAppsStore() {
        return null;
    }
    public View getContentView() {
        return this;// maybe?
    }
    public RecyclerViewFastScroller getScrollBar() {
        return null;
    }
    public FloatingHeaderView getFloatingHeaderView() {
        return null;
    }
    public SearchUiManager getSearchUiManager() {
        return null;
    }

    public boolean shouldContainerScroll(MotionEvent ev) { return branchSearchFragment.canScrollUp(); } // reused original method
    public AlphabeticalAppsList getApps() { return null;}

    public void onScrollUpEnd() { }
    public void highlightWorkTabIfNecessary() { }
    public void addSpringFromFlingUpdateListener(ValueAnimator animator, float velocity) { }
    public void onTabChanged(int pos) {}
    public void setupHeader() {}
    public void setLastSearchQuery(String query) {}
    public void onClearSearchResult() {}
    public void onSearchResultsChanged() {}

    // reused original method
    public void reset(boolean animate) {
        if (branchSearchFragment.isAdded()) {
            branchSearchFragment.updateQuery("");
        }
    }
}
