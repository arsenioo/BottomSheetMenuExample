package com.example.bottomsheetmenuexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Calendar;
import java.util.Date;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

@SuppressLint("ViewConstructor")
public class CustomMenuV3 extends BottomSheetMenu implements View.OnClickListener
{
    private static final int NEW_GAME_BUTTON = R.id.first_menu_button;//item_caption;

    private static final int BATTERY_BUTTON = R.id.third_menu_button;//item_icon;

    private DynamicArrowView menuControlButton;
    private BatteryLevelDrawable batteryDrawable;
    private AppCompatButton batteryButton;
    private View exitButton;
    private View topView;
    private View bottomView;
    private Context context;

    private float slideOffset;
    private int lastBottomSheetState = STATE_COLLAPSED;
    private boolean isLeftHandled;
    private boolean isMenuButtonEnabled = true;


    @SuppressLint("InflateParams")
    public CustomMenuV3(final Context context, ViewGroup parentView)
    {
        super(context);
        this.context = context;

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, Gravity.BOTTOM);
        parentView.addView(this, lp);

        AsyncLayoutInflater mLayoutInflater = new AsyncLayoutInflater(context);
        final AsyncLayoutInflater.OnInflateFinishedListener exitButtonViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(@NonNull View view, int resid, ViewGroup parent)
            {
                parent.addView(view);
                exitButton = view;
                view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // At this point the layout is complete and the
                        // dimensions of myView and any child views are known.
                        layoutExitButton();
                    }
                });
                updateLeftHandledAppearance();
            }
        };

        final AsyncLayoutInflater.OnInflateFinishedListener topViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(@NonNull View view, int resid, ViewGroup parent)
            {
                topView = view;
                setTopView(view);
                menuControlButton = topView.findViewById(R.id.closeBut);
                menuControlButton.setOnClickListener(CustomMenuV3.this);
                updateMenuAppearance();
                fadeButtonIn();
                fadeButtonOutWithDelay();
            }
        };
        final AsyncLayoutInflater.OnInflateFinishedListener bottomViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(@NonNull View view, int resid, ViewGroup p)
            {
                bottomView = view;
                setBottomView(view);
                batteryDrawable = new BatteryLevelDrawable();
                batteryButton = bottomView.findViewById(BATTERY_BUTTON);
//                batteryButton.setCompoundDrawablesWithIntrinsicBounds(null, batteryDrawable, null, null);
                updateMenuAppearance();
            }
        };

        mLayoutInflater.inflate(R.layout.menu_exit_button, parentView, exitButtonViewCallback);
        mLayoutInflater.inflate(R.layout.menu_top_part, null, topViewCallback);
        mLayoutInflater.inflate(R.layout.menu_bottom_part, null, bottomViewCallback);
    }

    private void applyRotation(int h, int w)
    {
        if (bottomView == null) return;
        View wrapView = bottomView.findViewById(R.id.fourth_menu_button);
        if (wrapView == null) return;
        FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams)wrapView.getLayoutParams();
        lp.setWrapBefore(h > w);
        wrapView.setLayoutParams(lp);
        wrapView.requestLayout();
    }

    private void drawBird(float offset)
    {
        if (!isMenuButtonEnabled) return;
        menuControlButton.setArrowPhase(offset);
        menuControlButton.invalidate();
    }

    private void layoutExitButton() {
        final int height = exitButton.getHeight();
        if (height == 0) return;            // Not measured, will be called later after layout

        exitButton.setTranslationY((slideOffset - 1) * height);
        exitButton.invalidate();
    }

    private void setExitButtonOffset(float offset) {
        if (slideOffset == offset || exitButton == null) return;    // Too early or no changes

        // May be we have to change visibility
        if (slideOffset != 0 && offset == 0) exitButton.setVisibility(View.GONE);           // Time to hide
        else if (slideOffset == 0 && offset != 0) exitButton.setVisibility(View.VISIBLE);   // Time to show

        slideOffset = offset;
        layoutExitButton();
    }


    @Override
    public void show()
    {
        super.show();
        fadeButtonIn();
        if (menuControlButton != null) menuControlButton.clearFocus();
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
        if (newState == STATE_COLLAPSED)
        {
            fadeButtonOutWithDelay();
        }
        else fadeButtonIn();

        if (lastBottomSheetState == STATE_COLLAPSED && newState != STATE_COLLAPSED)
        {
            //updateBatteryButton();
        }
        lastBottomSheetState = newState;
    }

    @Override
    public void bottomSheetOnSlide(View bottomSheet, float slideOffset)
    {
        super.bottomSheetOnSlide(bottomSheet, slideOffset);
        if (menuControlButton != null) drawBird(slideOffset);
        if (exitButton != null) setExitButtonOffset(slideOffset);
    }


    final Runnable fadeButtonOut = new Runnable() {
        @Override
        public void run() {
            menuControlButton.animateOpacity(0.3f);
        }
    };

    private void fadeButtonIn() {
        removeCallbacks(fadeButtonOut);
        menuControlButton.animateOpacity(1.0f);
    }

    private void fadeButtonOutWithDelay() {
        removeCallbacks(fadeButtonOut);
        postDelayed(fadeButtonOut, /*D.FADEOUT_MENU_BUTTON_TIMEOUT * 1000*/ 2000);
    }

    @Override
    public void onClick(View v) {
        if (v == menuControlButton) toggle();
    }


    final Rect menuRect = new Rect();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        return false;
//        if (isActive())
//        {
//            hide();
//            return true;
//        }
//        else return false;
//
//        // Filter initial touch event only
//        if (event.getAction() != MotionEvent.ACTION_DOWN) return false;
//
//        final int tapX = Math.round(event.getX());
//        final int tapY = Math.round(event.getY());
//
//        if (bottomView == null) return false;
//
//        bottomView.getGlobalVisibleRect(menuRect);
//        boolean tapOnVisible = menuRect.contains(tapX, tapY);
//
//        if (menuControlButton != null) {
//            menuControlButton.getGlobalVisibleRect(menuRect);
//            tapOnVisible |= menuRect.contains(tapX, tapY);
//        }
//
//        if (tapOnVisible) return false;
//
//        hide();
//        return true;
    }

    public void setLeftHandled(boolean leftHandled)
    {
        if (leftHandled == isLeftHandled) return;
        isLeftHandled = leftHandled;
        updateLeftHandledAppearance();
    }

    void setMenuButtonEnabled(boolean enabled)
    {
        if (isMenuButtonEnabled == enabled) return;
        isMenuButtonEnabled = enabled;
        updateMenuAppearance();
    }

    @SuppressLint("RtlHardcoded")
    private void updateLeftHandledAppearance() {
        if (exitButton == null) return;             // Too early, will be called later anyway

        final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)exitButton.getLayoutParams();
        lp.gravity = Gravity.TOP | (isLeftHandled? Gravity.LEFT: Gravity.RIGHT);
        exitButton.setLayoutParams(lp);
        exitButton.setBackgroundResource(isLeftHandled? R.drawable.bg_bottom_right_rounded: R.drawable.bg_bottom_left_rounded);
        exitButton.requestLayout();
    }

    private void updateMenuAppearance()
    {
        if (bottomView == null || menuControlButton == null) return;     // Too early, will be called later anyway

        bottomView.setBackgroundResource(isMenuButtonEnabled? R.drawable.bg_top_right_rounded: R.drawable.bg_top_rounded);
        menuControlButton.setVisibility(isMenuButtonEnabled? View.VISIBLE: View.GONE);
    }

    private final IntentFilter batteryInfoRequest = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    private void updateBatteryButton()
    {
        final Intent batteryInfo = context.registerReceiver(null, batteryInfoRequest);
        if (batteryInfo == null) return;

        final int level = batteryInfo.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final int scale = batteryInfo.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        final int status = batteryInfo.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
        final float batteryLevel = (100.0f * level) / scale;
        final boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING);
        batteryDrawable.setState(batteryLevel, isCharging);
        batteryButton.setText(SystemClock.currentThreadTimeMillis() + ""/*getCurrentTime()*/);
        batteryButton.invalidate();
    }

    String getCurrentTime()
    {
        final Date time = Calendar.getInstance().getTime();
        return DateFormat.getTimeFormat(context).format(time);
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

    class BatteryLevelDrawable extends Drawable {
        private boolean _isCharging;
        private float _batteryLevel = 146.0f;   // Percentage

        private VectorDrawableCompat _batteryDrawable;
        private final Paint textPaint;


        BatteryLevelDrawable() {
            textPaint = new Paint();
            final float textSize = getResources().getDimension(R.dimen.menu_item_image_height) / 3.0f;
            textPaint.setTextSize(textSize);
            textPaint.setColor(0xFF000000);
            textPaint.setAntiAlias(true);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTextAlign(Paint.Align.CENTER);
            setBatteryDrawable();
            setBounds(0, 0, 96, 80);
        }

        private void setBatteryDrawable()
        {
            final int batteryResource = _isCharging? R.drawable.ic_menu_battery_charge: R.drawable.ic_menu_battery_base;
            _batteryDrawable = VectorDrawableCompat.create(context.getResources(), batteryResource, context.getTheme());

            if (_batteryDrawable != null) {
//                setBounds(_batteryDrawable.getBounds());
            }
        }

        void setState(float level, boolean charging)
        {
            if (_isCharging == charging && _batteryLevel == level) return;

            if (_isCharging != charging)
            {
                setBatteryDrawable();
                _isCharging = charging;
            }

            _batteryLevel = level;
            final float[] hsv = {_batteryLevel * 1.2f, 1.0f, 0.9f};
            if (_batteryDrawable != null) {
                _batteryDrawable.setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(255, hsv), PorterDuff.Mode.SRC_IN));
            }
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawColor(0xFFD00000);
            _batteryDrawable.invalidateSelf();
            _batteryDrawable.draw(canvas);
            if (_isCharging) return;

            final int outValue = (int)(_batteryLevel + 0.5f);
            textPaint.setTextScaleX(outValue == 100? 0.85f: 1.0f);
            canvas.drawText(outValue + "%", 0, 0, textPaint);
        }

        @Override
        public void setAlpha(int i) {
            textPaint.setAlpha(i);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            textPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    public void testEnlarge() {     // TODO: Remove this
        android.view.ViewGroup.LayoutParams lp = menuControlButton.getLayoutParams();
        lp.width = lp.width * 8 / 7;
        lp.height = lp.height * 8 / 7;
        menuControlButton.setLayoutParams(lp);
        menuControlButton.requestLayout();
        lp = exitButton.getLayoutParams();
        lp.width = exitButton.getWidth() * 8 / 7;
        lp.height = exitButton.getHeight() * 8 / 7;
        exitButton.setLayoutParams(lp);
        exitButton.requestLayout();
    }

}
