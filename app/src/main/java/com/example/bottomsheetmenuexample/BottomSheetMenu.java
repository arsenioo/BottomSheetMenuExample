package com.example.bottomsheetmenuexample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.LinearLayout.VERTICAL;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

/**
 * Created by apc on 2019-05-15 for BottomSheetMenuExample
 */

public class BottomSheetMenu extends CoordinatorLayout  {
    private View _topView;
    private View _bottomView;
    private LinearLayout _menuContainerLayout;
    private BottomSheetBehavior _bottomSheetBehavior;
    public boolean mIsShowing = false;

    final LayoutParams menuPartsLayoutParams =
        new LayoutParams(MATCH_PARENT, WRAP_CONTENT);

    public BottomSheetMenu(Context context) {
        super(context);
        setFitsSystemWindows(true);
        _menuContainerLayout = new LinearLayout(context);
        _menuContainerLayout.setOrientation(VERTICAL);
        _bottomSheetBehavior = new BottomSheetBehavior(context, null);
        _bottomSheetBehavior.setHideable(false);

        final CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.setBehavior(_bottomSheetBehavior);

        this.addView(_menuContainerLayout, lp);
        setBottomSheetCallback();
    }

    public BottomSheetMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomSheetMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        if (_topView != null) _bottomSheetBehavior.setPeekHeight(_topView.getHeight());
    }

    public void setBottomView(View view)
    {
        if (_bottomView != null) _menuContainerLayout.removeView(_bottomView);
        _bottomView = view;
        if (view != null) _menuContainerLayout.addView(view, -1, menuPartsLayoutParams);
    }

    public void setTopView(View view)
    {
        if (_topView != null) _menuContainerLayout.removeView(_topView);
        _topView = view;
        if (view != null) {
            _menuContainerLayout.addView(view, 0, menuPartsLayoutParams);
        }
    }

    public void setHideAlarm()
    {
        removeCallbacks(hideMenu);
        postDelayed(hideMenu, /*D.HIDE_MENU_TIMEOUT * 1000*/ 4000);
    }

    public void bottomSheetOnStateChanged(View bottomSheet, int newState)
    {
        if (newState == STATE_EXPANDED)
        {
            mIsShowing = true;
            setHideAlarm();
        }
        else if (newState == STATE_COLLAPSED)
        {
            mIsShowing = false;
        }
    }

    public void bottomSheetOnSlide(View bottomSheet, float slideOffset) {}


    private void setBottomSheetCallback()
    {
        _bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
        {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState)
            {
                bottomSheetOnStateChanged(bottomSheet, newState);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset)
            {
                bottomSheetOnSlide(bottomSheet, slideOffset);
            }
        });
    }

    final Runnable hideMenu = new Runnable()
    {
        @Override
        public void run()
        {
            hide();
        }
    };

    public void hide()
    {
        _bottomSheetBehavior.setState(STATE_COLLAPSED);
    }

    public void show()
    {
        setHideAlarm();
        _bottomSheetBehavior.setState(STATE_EXPANDED);
    }

    public void toggle()
    {
        if (mIsShowing) hide();
        else show();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(_topView != null)
        {
            _bottomSheetBehavior.setPeekHeight(_topView.getMeasuredHeight());
        }
    }

}
