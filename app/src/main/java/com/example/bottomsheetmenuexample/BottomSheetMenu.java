package com.example.bottomsheetmenuexample;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

/**
 * Created by apc on 2019-05-15
 */
interface MenuActivationListener {
    void onActivated();
    void onCollapsed();
}

public class BottomSheetMenu {
    private BottomSheetBehavior bottomSheetBehavior;
    private float lastOffset;
    private MenuActivationListener activationListener;
    private int lastBottomSheetState = STATE_COLLAPSED;
    private View sheetView;

    BottomSheetMenu(@NonNull View sheetView, @NonNull View persistentMenuView) {
        this.sheetView = sheetView;
        bottomSheetBehavior = BottomSheetBehavior.from(sheetView);

        final BottomSheetCallback bottomSheetCallback = new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                bottomSheetOnStateChanged(bottomSheet, newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float offset) {
                bottomSheetOnSlide(bottomSheet, offset);
                lastOffset = offset;
            }
        };

        final View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (top != oldTop) bottomSheetBehavior.setPeekHeight(top);
            }
        };

        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);
        persistentMenuView.addOnLayoutChangeListener(layoutChangeListener);
        hide();
    }

    float getSlideProgress() {
        return lastOffset;
    }

    @SuppressWarnings("WeakerAccess")
    public void setActivationListener(MenuActivationListener activationListener) {
        this.activationListener = activationListener;
    }

    public void bottomSheetOnStateChanged(View bottomSheet, int newState) {
        if (activationListener != null) {
            if (lastBottomSheetState == STATE_COLLAPSED && newState != STATE_COLLAPSED) {
                activationListener.onActivated();
            } else if (lastBottomSheetState != STATE_COLLAPSED && newState == STATE_COLLAPSED) {
                activationListener.onCollapsed();
            }
        }
        lastBottomSheetState = newState;
    }

    public void bottomSheetOnSlide(View bottomSheet, float slideOffset) {}

    @SuppressWarnings("WeakerAccess")
    public boolean isEnabled() {
        return sheetView.getVisibility() == View.VISIBLE;
    }

    @SuppressWarnings("unused")
    public void setEnabled(boolean isEnabled) {
        sheetView.setVisibility(isEnabled? View.VISIBLE: View.INVISIBLE);
    }

    boolean isActive()
    {
        return (bottomSheetBehavior != null && bottomSheetBehavior.getState() == STATE_EXPANDED);
    }

    public void hide()
    {
        if (isEnabled()) bottomSheetBehavior.setState(STATE_COLLAPSED);
    }

    public void show()
    {
        if (isEnabled()) bottomSheetBehavior.setState(STATE_EXPANDED);
    }

    void toggle()
    {

        if (isEnabled()) {
            if (isActive()) hide(); else show();
        }
    }

    public static class WithAnimatedGripButton extends BottomSheetMenu implements View.OnClickListener
    {
        private DynamicArrowView gripButton;
        private int fadeDelay;
        private float fadedOpacity = 0.3f;
        private boolean isGripButtonEnabled;

        WithAnimatedGripButton(@NonNull View sheetView, @NonNull View persistentMenuView, @NonNull DynamicArrowView gripButton)
        {
            super(sheetView, persistentMenuView);
            this.gripButton = gripButton;
            gripButton.setOnClickListener(this);
        }

        @Override
        public void show()
        {
            super.show();
            if (isGripButtonEnabled) {
                fadeButtonIn();
                gripButton.clearFocus();
            }
        }

        @Override
        public void hide()
        {
            super.hide();
            if (isGripButtonEnabled) gripButton.requestFocus();
        }

        private final Runnable fadeButtonOut = new Runnable() {
            @Override
            public void run() {
                gripButton.animateOpacity(fadedOpacity);
            }
        };

        private void fadeButtonIn() {
            gripButton.removeCallbacks(fadeButtonOut);
            gripButton.animateOpacity(1.0f);
        }

        private void fadeButtonOutWithDelay() {
            gripButton.removeCallbacks(fadeButtonOut);
            gripButton.postDelayed(fadeButtonOut, fadeDelay);
        }

        @Override
        public void bottomSheetOnStateChanged(View bottomSheet, int newState)
        {
            super.bottomSheetOnStateChanged(bottomSheet, newState);
            if (!isGripButtonEnabled) return;

            if (newState == STATE_COLLAPSED) fadeButtonOutWithDelay();
            else fadeButtonIn();
        }

        @Override
        public void bottomSheetOnSlide(View bottomSheet, float slideOffset)
        {
            super.bottomSheetOnSlide(bottomSheet, slideOffset);
            if (!isGripButtonEnabled) return;
            gripButton.setArrowPhase(slideOffset);
            gripButton.invalidate();
        }

        @Override
        public void onClick(View v) {
            if (v == gripButton) toggle();
        }

        @SuppressWarnings("WeakerAccess")
        public void setGripButtonEnabled(boolean enabled)
        {
            if (enabled == isGripButtonEnabled) return;
            isGripButtonEnabled = enabled;

            if (enabled) {
                gripButton.setArrowPhase(getSlideProgress());
                gripButton.setVisibility(View.VISIBLE);
                fadeButtonIn();
                fadeButtonOutWithDelay();
            }
            else
            {
                gripButton.setVisibility(View.GONE);
                gripButton.removeCallbacks(fadeButtonOut);
            }
            gripButton.invalidate();
        }

        @SuppressWarnings("WeakerAccess")
        public void setFadeDelay(int delay) {
            fadeDelay = delay;
        }

        @SuppressWarnings("WeakerAccess")
        public void setFadedOpacity(float opacity) {
            fadedOpacity = opacity;
        }

        public static class WithUpperPanel extends WithAnimatedGripButton {
            private View upperPanel;

            WithUpperPanel(@NonNull View sheetView, @NonNull View persistentMenuView, @NonNull DynamicArrowView gripButton, @NonNull View upperPanel) {
                super(sheetView, persistentMenuView, gripButton);
                this.upperPanel = upperPanel;
            }

            @Override
            public void bottomSheetOnSlide(View bottomSheet, float offset) {
                final float lastOffset = getSlideProgress();

                // May be we have to change visibility
                if (lastOffset != 0 && offset == 0) upperPanel.setVisibility(View.GONE);            // Time to hide
                else if (lastOffset == 0 && offset != 0) upperPanel.setVisibility(View.VISIBLE);    // Time to show

                upperPanel.setTranslationY((offset - 1) * upperPanel.getHeight());
                upperPanel.invalidate();
                super.bottomSheetOnSlide(bottomSheet, offset);
            }
        }
    }
}

