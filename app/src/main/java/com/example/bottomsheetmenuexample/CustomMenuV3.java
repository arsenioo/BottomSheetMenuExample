package com.example.bottomsheetmenuexample;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import androidx.asynclayoutinflater.view.AsyncLayoutInflater;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CustomMenuV3 extends BottomSheetMenu implements View.OnClickListener
{
    private View menuControlButton;
    private View exitButton;
    private boolean _menuButtonEnabled;
    private ViewGroup parent;
    private View topView;
    private View bottomView;


    public CustomMenuV3(final Context context)
    {
        super(context);
        AsyncLayoutInflater mLayoutInflater = new AsyncLayoutInflater(context);
        final AsyncLayoutInflater.OnInflateFinishedListener topViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup parent)
            {
                topView = view;
                setTopView(view);
            }
        };
        final AsyncLayoutInflater.OnInflateFinishedListener bottomViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup parent)
            {
                bottomView = view;
                setBottomView(view);
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;
                applyRotation(height, width);
            }
        };
        mLayoutInflater.inflate(R.layout.menu_top_part, null, topViewCallback);
        mLayoutInflater.inflate(R.layout.menu_bottom_part, null, bottomViewCallback);
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        applyRotation(h, w);
    }

    private void applyRotation(int h, int w)
    {
        if (bottomView == null) return;
        View foursButton = bottomView.findViewById(R.id.fourth_menu_button);
        if (foursButton == null) return;
        FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams)foursButton.getLayoutParams();
        lp.setWrapBefore(h > w);
        foursButton.setLayoutParams(lp);
        foursButton.invalidate();
        bottomView.invalidate();
        bottomView.requestLayout();
    }

/*    private void drawBird(float slideOffset)
    {
        if(menuControlButton == null || menuControlButton.getVisibility() != View.VISIBLE) return;
        Drawable[] layers = new Drawable[2];
        Resources r = getResources();
        final float birdWidth = mMenuButtonWidth / 2;
        final float birdHeight =  mMenuButtonHeight / 4;
        layers[0] = paintDrawable(slideOffset, birdWidth, birdHeight, Color.BLACK, r.getDimensionPixelSize(R.dimen.bird_stroke_width) * 2, true);
        layers[1] = paintDrawable(slideOffset, birdWidth, birdHeight, Color.WHITE, r.getDimensionPixelSize(R.dimen.bird_stroke_width), false);
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        ImageView image = menuControlButton.findViewById(R.id.closeButImage);
        image.setImageDrawable(layerDrawable);
        image.requestLayout();
    }*/

/*    private void drawExitButton(float slideOffset)
    {
        if(exitButton == null)
        {
            exitButton = mLayoutInflater.inflate(R.layout.menu_exit_button, parent, false);
            exitButton.setOnClickListener(this);
            parent.addView(exitButton);
        }
        int width = exitButton.getMeasuredWidth();
        if (width == 0)
        {
            exitButton.measure(0, 0);
            width = exitButton.getMeasuredWidth();
        }

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)exitButton.getLayoutParams();
        params.topMargin =  (int)(-1 * width * (1 - slideOffset));
        exitButton.setLayoutParams(params);
        exitButton.invalidate();
        exitButton.requestLayout();
        exitButton.bringToFront();
    }*/

    public void removeExitButton()
    {
        if (exitButton == null) return;
        parent.removeView(exitButton);
        exitButton = null;
    }

/*    final Rect zeroRect = new Rect();

    private ShapeDrawable paintDrawable(float offset, float width, float height, int color, int strokeWidth, boolean shadowEnabled)
    {
        ShapeDrawable drawable = new ShapeDrawable(TriangleShape.create(width, height, offset));
        Paint arrowPaint = drawable.getPaint();
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setColor(color);
        arrowPaint.setStrokeWidth(strokeWidth);
        arrowPaint.setAntiAlias(true);
        if (shadowEnabled) arrowPaint.setShadowLayer(strokeWidth / 4, 0, 0, Color.BLACK);
        drawable.setIntrinsicWidth((int)width);
        drawable.setIntrinsicHeight((int)height);

        drawable.setPadding(zeroRect);                // BugFix for SFA-222, don't change this line:
        return drawable;
    }*/

/*    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeBut:
                if (currentState == BottomSheetBehavior.STATE_COLLAPSED)
                {
                    show();
                }
                if (currentState == BottomSheetBehavior.STATE_EXPANDED) hide();
                break;

            case R.id.exitButton:
                if (D.DEBUG) Log.d("Bottom Sheet Behaviour", "ClickExit");
                CustomMenuItem cmi = new CustomMenuItem();
                cmi.setId(BottomMenuHandler.EXIT_MENU_ID);
                mListener.MenuItemSelectedEvent(cmi);
                if (mHideOnSelect) hide();
                break;
        }
    }*/
    @Override
    public void show()
    {
        super.show();
        fadeButtonIn();
        if (menuControlButton != null)  menuControlButton.clearFocus();
       /* TableLayout t = mView.findViewById(R.id.custom_menu_table);
        menuControlButton.setNextFocusRightId(t.getChildAt(0).findViewById(R.id.custom_menu_item_icon).getId());*/
    }

    @Override
    public void hide()
    {
        super.hide();
        if (menuControlButton != null) menuControlButton.requestFocus();
    }


    @Override
    public void bottomSheetOnStateChanged(View bottomSheet, int newState)
    {

        super.bottomSheetOnStateChanged(bottomSheet, newState);
        if (newState == BottomSheetBehavior.STATE_EXPANDED)
        {
            setFadeOutAlarm();
        }
        else if(newState == BottomSheetBehavior.STATE_COLLAPSED)
        {
            removeExitButton();
        }
        else
        {
            fadeButtonIn();
        }
    }

    @Override
    public void bottomSheetOnSlide(View bottomSheet, float slideOffset)
    {
        super.bottomSheetOnSlide(bottomSheet, slideOffset);
/*        drawBird(slideOffset);
        drawExitButton(slideOffset);*/
    }

    private void setFadeOutAlarm()
    {
        handler.removeCallbacks(fadeButtonOut);
        handler.postDelayed(fadeButtonOut, /*D.FADEOUT_MENU_BUTTON_TIMEOUT * 1000*/ 4000);
    }



   /* private void handleSelectionChange(View view, boolean isFocused)
    {
        if (view == null) return;

        if (isFocused)
        {
            if (view instanceof ImageView) ((ImageView)view).setColorFilter(_menuSelectionFilter);
            else view.getBackground().setColorFilter(_menuSelectionFilter);
        }
        else
        {
            if (view instanceof ImageView) ((ImageView)view).clearColorFilter();
            else view.getBackground().clearColorFilter();
        }
    }*/

    private void fadeButtonIn()
    {
        if (android.os.Build.VERSION.SDK_INT < 16 || menuControlButton == null || menuControlButton.getVisibility() != View.VISIBLE) return;
        ImageView image = menuControlButton.findViewById(R.id.closeButImage);
        if (image == null) return;
        AlphaAnimation animation1 = new AlphaAnimation(image.getAlpha(), 1.0f);
        animation1.setDuration(500);
        image.setAlpha(1f);
        animation1.setFillAfter(true);
        image.startAnimation(animation1);
    }

    private void fadeButtonOut()
    {
        if (android.os.Build.VERSION.SDK_INT < 16 || menuControlButton == null || menuControlButton.getVisibility() != View.VISIBLE) return;
        ImageView image = menuControlButton.findViewById(R.id.closeButImage);
        if (image == null) return;
        AlphaAnimation animation1 = new AlphaAnimation(image.getAlpha(), 0.5f);
        animation1.setDuration(500);
        image.setAlpha(0.5f);
        animation1.setFillAfter(true);
        image.startAnimation(animation1);
    }

    private  Runnable fadeButtonOut = new Runnable()
    {
        @Override
        public  void run()
        {
            fadeButtonOut();
        }
    };

    public void pause()
    {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeBut:
                if (currentState == BottomSheetBehavior.STATE_COLLAPSED)
                {
                    show();
                }
                if (currentState == BottomSheetBehavior.STATE_EXPANDED) hide();
                break;

            case R.id.exitButton:
/*
                CustomMenuItem cmi = new CustomMenuItem();
                cmi.setId(BottomMenuHandler.EXIT_MENU_ID);
                mListener.MenuItemSelectedEvent(cmi);
                if (mHideOnSelect) hide();*/
                break;
        }
    }


/*
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        // Filter initial touch event only
        if (event.getAction() != MotionEvent.ACTION_DOWN) return false;

        final int tapX = Math.round(event.getX());
        final int tapY = Math.round(event.getY());

        mView.findViewById(R.id.custom_menu_table).getGlobalVisibleRect(menuRect);

        boolean tapOnVisible = menuRect.contains(tapX, tapY);

        if (_menuButtonEnabled)
        {
            menuControlButton.getGlobalVisibleRect(menuRect);
            tapOnVisible |= menuRect.contains(tapX, tapY);
        }

        if (tapOnVisible) return false;

        if (mIsShowing) return true;

        // Process a touch in invisible area in a hidden state
        return mListener != null && mListener.onFilterTouchEvent(event);

    }
*/

}
