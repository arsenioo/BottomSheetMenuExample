package com.example.bottomsheetmenuexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.BatteryManager;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Calendar;
import java.util.Date;

public class CustomMenuV3 extends BottomSheetMenu implements View.OnClickListener
{
    private static final int NEW_GAME_BUTTON_TEXT = R.id.first_menu_item_caption;

    private static final int BATTERY_BUTTON_ICON = R.id.third_menu_item_icon;
    private static final int BATTERY_BUTTON_TEXT = R.id.third_menu_item_caption;
    private View menuControlButton;
    private View exitButton;
    private boolean _menuButtonEnabled;
    private View topView;
    private View bottomView;
    private  ViewGroup parent;
    private int mMenuButtonHeight;
    private int mMenuButtonWidth;
    private boolean isLefthandled;
    private Context mContext;


    public CustomMenuV3(final Context context, ViewGroup parentView, boolean leftHandled, boolean menuButtonEnabled)
    {
        super(context);
        mContext = context;
        parent = parentView;
        isLefthandled = leftHandled;
        _menuButtonEnabled = menuButtonEnabled;

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
                context.registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            }
        };

        mLayoutInflater.inflate(R.layout.menu_exit_button, parent, exitButtonViewCallback);
        mLayoutInflater.inflate(R.layout.menu_top_part, null, topViewCallback);
        mLayoutInflater.inflate(R.layout.menu_bottom_part, null, bottomViewCallback);
    }

    private void initMenuControlButton()
    {
        menuControlButton = topView.findViewById(R.id.closeBut);
        updateMenuButtonVisibility();
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
        params.gravity = Gravity.TOP | (isLefthandled?Gravity.LEFT:Gravity.RIGHT);
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
                if (!mIsShowing)
                {
                    show();
                }
                else hide();
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
            return false;
        }
        hide();
        return true;
    }

    public void setLaftHandled(boolean leftHandled)
    {
        isLefthandled = leftHandled;
    }

    public void setMenuButtonEnabled(boolean enabled)
    {
        _menuButtonEnabled = enabled;
        updateMenuButtonVisibility();
    }

    private void updateMenuButtonVisibility()
    {
        menuControlButton.setVisibility(_menuButtonEnabled?View.VISIBLE:View.GONE);
    }

    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action))
            {
                int level = intent.getIntExtra("level", 0);
                int scale = intent.getIntExtra("scale", 100);
                float _batteryValue = (100.0f * level) / scale;
                int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);
                boolean _batteryCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING);
                updateBattery(_batteryValue, _batteryCharging);
            }
        }
    };

    public void updateBattery(float _batteryValue, boolean _batteryCharging)
    {

        TextView iconImage = bottomView.findViewById(BATTERY_BUTTON_ICON);
        if (iconImage == null) return;
        final float[] hsv = {_batteryValue * 1.2f, 1.0f, 0.9f};
        final int batteryResource = _batteryCharging? R.drawable.ic_menu_battery_charge: R.drawable.ic_menu_battery_base;

        final VectorDrawableCompat batteryDrawable = VectorDrawableCompat.create(mContext.getResources(), batteryResource, mContext.getTheme());
        if (batteryDrawable != null) batteryDrawable.setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(255, hsv), PorterDuff.Mode.SRC_IN));
        iconImage.setBackgroundDrawable(batteryDrawable);

        final int outValue = (int)(_batteryValue + 0.5f);
        final float textSize = getResources().getDimensionPixelSize(R.dimen.menu_item_image_height) / 3.0f;
        iconImage.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        iconImage.setTextColor(0xFF000000);

        iconImage.setTextScaleX(outValue == 100? 0.85f: 1.0f);

        final String iconText = _batteryCharging? "": outValue + "%";
        iconImage.setText(iconText);

        View itemText = bottomView.findViewById(BATTERY_BUTTON_TEXT);
        ((TextView) itemText).setText(getCurrentTime());
    }

    public String getCurrentTime()
    {
        Calendar cal = Calendar.getInstance();
        Date time = cal.getTime();
        return DateFormat.getTimeFormat(mContext).format(time);
    }

/*    public void restoreMenuButtons()
    {
        if (!D.MULTIPLAY)
        {
            restoreMenuNewGame();
            restoreMenuSolution();
        }
        else replaceMenuNewGameToEndGame();

    }*/

/*    public void replaceMenuNewGameToEndGame(){replaceMenuItem(NEW_GAME_BUTTON,   getResources().getString(R.string.endGame));}
    public void replaceMenuNewGameToResign(){replaceMenuItem(NEW_GAME_BUTTON,   getResources().getString(R.string.resign));}
    public void replaceMenuNewGameToClaim(){replaceMenuItem(NEW_GAME_BUTTON,   getResources().getString(R.string.claim_victory));}
    public void restoreMenuNewGame(){replaceMenuItem(NEW_GAME_BUTTON,   getResources().getString(R.string.new_g));}*/



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
