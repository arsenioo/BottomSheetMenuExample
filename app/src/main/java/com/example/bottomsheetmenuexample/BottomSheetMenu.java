package com.example.bottomsheetmenuexample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

/**
 * Created by apc on 2019-05-15 for BottomSheetMenuExample
 */

public class BottomSheetMenu extends CoordinatorLayout  {
    private ViewGroup _topView;
    private ViewGroup _bottomView;
    private int topViewHeight = 0;
    private BottomSheetBehavior bottomSheetBehavior;
    public boolean mIsShowing = false;

    final LayoutParams matchParentParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    public BottomSheetMenu(Context context) {
        super(context);
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.bottom_menu, BottomSheetMenu.this, true);
        _topView = findViewById(R.id.topPart);
        _bottomView = findViewById(R.id.bottomPart);
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
    }

    public void setBottomView(View view)
    {
        _bottomView.removeAllViews();
        _bottomView.addView(view, matchParentParams);
        _bottomView.invalidate();
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setPeekHeight(topViewHeight);
        setBottomSheetCallback();
    }

    public void setTopView(View view)
    {
        _topView.removeAllViews();
        _topView.addView(view, matchParentParams);
    }

    public void setHideAlarm()
    {
        removeCallbacks(hideMenu);
        postDelayed(hideMenu, /*D.HIDE_MENU_TIMEOUT * 1000*/ 4000);
    }

    public void bottomSheetOnStateChanged(View bottomSheet, int newState)
    {
        if (newState == BottomSheetBehavior.STATE_EXPANDED)
        {
            mIsShowing = true;
            setHideAlarm();
        }
        else if(newState == BottomSheetBehavior.STATE_COLLAPSED)
        {
            mIsShowing = false;
        }
    }

    public void bottomSheetOnSlide(View bottomSheet, float slideOffset) {}


    private void setBottomSheetCallback()
    {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback()
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

    Runnable hideMenu = new Runnable()
    {
        @Override
        public void run()
        {
            if (bottomSheetBehavior != null) hide();
        }
    };

    public void hide()
    {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void show()
    {
        setHideAlarm();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void toggle()
    {
        if (mIsShowing) hide();
        else show();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if(_topView != null)
        {
            _topView.measure(0, 0);
            topViewHeight = _topView.getMeasuredHeight();
            if (bottomSheetBehavior != null) bottomSheetBehavior.setPeekHeight(topViewHeight);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
