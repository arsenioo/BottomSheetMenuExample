package com.example.bottomsheetmenuexample;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CustomMenuV3 extends BottomSheetMenu implements View.OnClickListener
{
    final static PorterDuffColorFilter _menuSelectionFilter = new PorterDuffColorFilter(0xFFFFFF00, PorterDuff.Mode.SRC_IN);
    private View menuControlButton;
    private View exitButton;
    private boolean _menuButtonEnabled = true;
    private View topView;
    private View bottomView;
    private  ViewGroup parent;
    private int mMenuButtonHeight;
    private int mMenuButtonWidth;


    public CustomMenuV3(final Context context, ViewGroup parentView)
    {
        super(context);
        parent = parentView;
        AsyncLayoutInflater mLayoutInflater = new AsyncLayoutInflater(context);
        final AsyncLayoutInflater.OnInflateFinishedListener exitButtonViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup parent)
            {
                exitButton = view;
                parent.addView(exitButton);
                drawExitButton(0);
            }
        };

        final AsyncLayoutInflater.OnInflateFinishedListener topViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup parent)
            {
                topView = view;
                initMenuControlButton();
                setTopView(view);
                drawBird(0);
            }
        };
        final AsyncLayoutInflater.OnInflateFinishedListener bottomViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup p)
            {
                bottomView = view;
                setBottomView(view);
                int width = parent.getMeasuredWidth();
                int height = parent.getMeasuredHeight();
                applyRotation(height, width);
                for(int index=0; index < ((ViewGroup)bottomView).getChildCount(); ++index)
                {
                    View nextChild = ((ViewGroup)bottomView).getChildAt(index);
                    nextChild.setOnFocusChangeListener(new OnFocusChangeListener()
                    {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus)
                        {
                            setHideAlarm();
                        }
                    });
                }
            }
        };

        mLayoutInflater.inflate(R.layout.menu_exit_button, parent, exitButtonViewCallback);
        mLayoutInflater.inflate(R.layout.menu_top_part, null, topViewCallback);
        mLayoutInflater.inflate(R.layout.menu_bottom_part, null, bottomViewCallback);
    }

    private void initMenuControlButton()
    {
        menuControlButton = topView.findViewById(R.id.closeBut);
        menuControlButton.setVisibility(_menuButtonEnabled?View.VISIBLE:View.GONE);
        menuControlButton.setOnClickListener(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)menuControlButton.getLayoutParams();
        mMenuButtonHeight = params.height;
        mMenuButtonWidth = params.width = (int)(mMenuButtonHeight * 1.2);
        menuControlButton.setLayoutParams(params);
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

    private void drawBird(float slideOffset)
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
    }

    private void drawExitButton(float slideOffset)
    {
        if(exitButton == null) return;
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
    }


    final Rect zeroRect = new Rect();

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
    }

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
            fadeButtonOutWithDelay();
        }
        else if(newState == BottomSheetBehavior.STATE_COLLAPSED)
        {
          //  removeExitButton();
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
        drawBird(slideOffset);
        drawExitButton(slideOffset);
    }

    private void handleSelectionChange(View view, boolean isFocused)
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
    }

    private void fadeAnimation(float finalAlpha)
    {
        if (android.os.Build.VERSION.SDK_INT < 16 || menuControlButton == null || menuControlButton.getVisibility() != View.VISIBLE) return;
        ImageView image = menuControlButton.findViewById(R.id.closeButImage);
        if (image == null) return;
        AlphaAnimation animation1 = new AlphaAnimation(image.getAlpha(), finalAlpha);
        animation1.setDuration(500);
        image.setAlpha(finalAlpha);
        animation1.setFillAfter(true);
        image.startAnimation(animation1);
    }

    final Runnable fadeButtonOut = new Runnable() {
        @Override
        public void run() {
            fadeAnimation(0.5f);
        }
    };

    private void fadeButtonIn() {
        fadeAnimation(1.0f);
    }

    private void fadeButtonOutWithDelay() {
        removeCallbacks(fadeButtonOut);
        postDelayed(fadeButtonOut, /*D.FADEOUT_MENU_BUTTON_TIMEOUT * 1000*/ 4000);
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
        }
    }


    final Rect menuRect = new Rect();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        // Filter initial touch event only
        if (event.getAction() != MotionEvent.ACTION_DOWN) return false;

        final int tapX = Math.round(event.getX());
        final int tapY = Math.round(event.getY());

        findViewById(R.id.bottomPart).getGlobalVisibleRect(menuRect);

        boolean tapOnVisible = menuRect.contains(tapX, tapY);

        if (_menuButtonEnabled)
        {
            menuControlButton.getGlobalVisibleRect(menuRect);
            tapOnVisible |= menuRect.contains(tapX, tapY);
        }

        if (tapOnVisible)
        {
            ;
            return false;
        }
        hide();
        return true;
    }

    /**
     * Wrapper around {@link PathShape}
     * that creates a shape with a triangular path (pointing up or down).
     */
    static class TriangleShape extends PathShape
    {
        TriangleShape(@NonNull Path path, float stdWidth, float stdHeight) {
            super(path, stdWidth, stdHeight);
        }

        static TriangleShape create(float width, float height, float phase)
        {
            Path triangularPath = new Path();
            phase = (float) Math.sin(Math.PI / 2. * phase); //for better "bird" rotation simulation
            float yEnds = height * (1- phase);
            float yCenter = height * phase;

                triangularPath.moveTo(0, yEnds);
                triangularPath.lineTo(width / 2, yCenter);
                triangularPath.lineTo(width, yEnds);

            return new TriangleShape(triangularPath, width, height);
        }

    }
}
