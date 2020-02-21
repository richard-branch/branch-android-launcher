package io.branch.search.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogProto;

import com.android.launcher3.R;

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

        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, branchSearchFragment)
                .commit();
    }

    public void hideFragment() {
        Fragment branchDiscoveryFragment = branchSearchFragment.getChildFragmentManager().findFragmentById(R.id.branchapp_fragment_container);

        if (branchDiscoveryFragment != null) {
            branchSearchFragment.getChildFragmentManager().beginTransaction()
                    .hide(branchDiscoveryFragment)
                    .commit();
        }
    }

    public void showFragment() {
        Fragment branchDiscoveryFragment = branchSearchFragment.getChildFragmentManager().findFragmentById(R.id.branchapp_fragment_container);

        if (branchDiscoveryFragment != null) {
            branchSearchFragment.getChildFragmentManager().beginTransaction()
                    .show(branchDiscoveryFragment)
                    .commit();
        }
    }

    public boolean isFragmentHidden() {
        Fragment branchDiscoveryFragment = branchSearchFragment.getChildFragmentManager().findFragmentById(R.id.branchapp_fragment_container);

        if (branchDiscoveryFragment != null) {
            return branchDiscoveryFragment.isHidden();
        }

        return false;
    }

    public void resetToInitialState() {
        View scrollView = findViewById(R.id.branchapp_results_view);
        EditText editText = findViewById(R.id.branchapp_edit_text);

        if (editText != null) {
            editText.setText(null);

            if (scrollView.getScrollY() == 0) {
                editText.requestFocus();

                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            } else {
                scrollView.scrollTo(0, 0);
            }
        }
    }

    public void hideKeyboard() {
        post(() -> {
            EditText editText = findViewById(R.id.branchapp_edit_text);

            if (editText != null) {
                editText.clearFocus();

                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
    }

    public boolean isScrolled() {
        return findViewById(R.id.branchapp_results_view).getScrollY() > 0;
    }

// empty functions from interfaces
    public void onDropCompleted(View target, DropTarget.DragObject d, boolean success) {
    }

    @Override
    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
    }

// empty functions to remove copile errors
    public void reset(boolean animate) {
    }

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
