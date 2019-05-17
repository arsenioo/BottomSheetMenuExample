package com.example.bottomsheetmenuexample;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

/**
 * Created by apc on 2019-05-15 for BottomSheetMenuExample
 */

public class BottomSheetMenu extends CoordinatorLayout {
    private ViewGroup _topView;
    private ViewGroup _bottomView;
    private int topViewHeight = 0;
    private BottomSheetBehavior bottomSheetBehavior;
    private View menuControlButton;
    private View exitButton;
    private int currentState;
    Handler handler;
    private boolean _menuButtonEnabled;

    final LayoutParams matchParentParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    public BottomSheetMenu(Context context) {
        super(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        _bottomView.addView(view);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetBehavior.setPeekHeight(topViewHeight);
        currentState = BottomSheetBehavior.STATE_EXPANDED;
    }

    public void setTopView(View view)
    {
        _topView.removeAllViews();
        _topView.addView(view);
        _topView.measure(0,0);
        topViewHeight = _topView.getMeasuredHeight();
        if(bottomSheetBehavior != null) bottomSheetBehavior.setPeekHeight(topViewHeight);
    }
}
