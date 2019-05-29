package com.example.bottomsheetmenuexample;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

/**
 * Created by apc on 2019-05-15 for BottomSheetMenuExample
 */

public class BottomSheetMenu {
    private BottomSheetBehavior _bottomSheetBehavior;
    private View _persistentMenuView;

    BottomSheetMenu(@NonNull View sheetView, @IdRes int waterlineViewId) {
        _persistentMenuView = sheetView.findViewById(waterlineViewId);
        _bottomSheetBehavior = BottomSheetBehavior.from(sheetView);

        final BottomSheetCallback bottomSheetCallback = new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                bottomSheetOnStateChanged(bottomSheet, newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheetOnSlide(bottomSheet, slideOffset);
            }
        };

        final View waterlineView = sheetView.findViewById(waterlineViewId);
        final OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // At this point the layout is complete and the
                // dimensions of myView and any child views are known.
                _bottomSheetBehavior.setPeekHeight(waterlineView.getTop());
            }
        };

        _bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
        sheetView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    @NonNull View getPersistentMenuView() {
        return _persistentMenuView;
    }

    public void bottomSheetOnStateChanged(View bottomSheet, int newState) {}

    public void bottomSheetOnSlide(View bottomSheet, float slideOffset) {}

    boolean isActive()
    {
        return (_bottomSheetBehavior != null && _bottomSheetBehavior.getState() == STATE_EXPANDED);
    }

    public void hide()
    {
        _bottomSheetBehavior.setState(STATE_COLLAPSED);
    }

    public void show()
    {
        _bottomSheetBehavior.setState(STATE_EXPANDED);
    }

    void toggle()
    {
        if (isActive()) hide();
        else show();
    }
}
