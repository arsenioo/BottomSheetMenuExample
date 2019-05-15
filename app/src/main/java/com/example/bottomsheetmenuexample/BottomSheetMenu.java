package com.example.bottomsheetmenuexample;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * Created by apc on 2019-05-15 for BottomSheetMenuExample
 */

public class BottomSheetMenu extends LinearLayout {
    private View _topView;
    private View _bottomView;

    final LayoutParams matchParentParams =
        new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    public BottomSheetMenu(Context context) {
        super(context);
    }

    public BottomSheetMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomSheetMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BottomSheetMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setBottomView(View view)
    {
        if (_bottomView != null) this.removeView(_bottomView);
        _bottomView = view;
        if (view != null) this.addView(view, -1, matchParentParams);
    }

    public void setTopView(View view)
    {
        if (_topView != null) this.removeView(_topView);
        _topView = view;
        if (view != null) this.addView(view, 0, matchParentParams);
    }

}
