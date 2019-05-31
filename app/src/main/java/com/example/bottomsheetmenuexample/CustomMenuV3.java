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
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.Calendar;
import java.util.Date;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

class CustomMenuV3
{

    private static final int NEW_GAME_BUTTON = R.id.menuButton1;
    private static final int BATTERY_BUTTON = R.id.menuButton3;

    private Context context;
    private DynamicArrowView gripButton;
    private BatteryLevelDrawable batteryDrawable;
    private AppCompatButton batteryButton;
    private View exitButton;
    private View persistentMenuView;

    private MenuWithListener menu;

    private static class MenuWithListener extends BottomSheetMenu.WithAnimatedGripButton.WithUpperPanel {
        private int lastBottomSheetState = STATE_COLLAPSED;

        MenuWithListener(@NonNull View sheetView, @NonNull View persistentMenuView, @NonNull DynamicArrowView gripButton, @NonNull View upperPanel) {
            super(sheetView, persistentMenuView, gripButton, upperPanel);
        }

        @Override
        public void bottomSheetOnStateChanged(View bottomSheet, int newState) {
            super.bottomSheetOnStateChanged(bottomSheet, newState);
            if (lastBottomSheetState == STATE_COLLAPSED && newState != STATE_COLLAPSED) {
                //updateBatteryButton();
            }
            lastBottomSheetState = newState;
        }
    }

    CustomMenuV3(@NonNull Context context, @NonNull ViewGroup activityRoot, @NonNull View persistentMenuView)
    {
        this.context = context;
        this.persistentMenuView = persistentMenuView;
        gripButton = activityRoot.findViewById(R.id.gripButton);
        exitButton = activityRoot.findViewById(R.id.exitButton);

        menu = new MenuWithListener(
            activityRoot.findViewById(R.id.menuLayout), persistentMenuView, gripButton, exitButton);

        menu.setFadeDelay(300);
        menu.setFadedOpacity(0.3f);
        batteryButton = activityRoot.findViewById(BATTERY_BUTTON);

        batteryDrawable = new BatteryLevelDrawable();
//        batteryButton.setCompoundDrawablesWithIntrinsicBounds(null, batteryDrawable, null, null);
    }

    @SuppressLint("RtlHardcoded")
    void setLeftHandled(boolean leftHandled)
    {
        final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)exitButton.getLayoutParams();
        lp.gravity = Gravity.TOP | (leftHandled? Gravity.LEFT: Gravity.RIGHT);
        exitButton.setLayoutParams(lp);
        exitButton.setBackgroundResource(leftHandled? R.drawable.bg_bottom_right_rounded: R.drawable.bg_bottom_left_rounded);
        exitButton.requestLayout();
    }

    void setGripButtonEnabled(boolean enabled)
    {
        persistentMenuView.setBackgroundResource(enabled? R.drawable.bg_top_right_rounded: R.drawable.bg_top_rounded);
        persistentMenuView.invalidate();
        menu.setGripButtonEnabled(enabled);
    }

    @SuppressWarnings("unused")
    void show() {
        menu.show();
    }

    void hide() {
        menu.hide();
    }

    @SuppressWarnings("unused")
    void toggle() {
        menu.toggle();
    }

    boolean isActive() {
        return menu.isActive();
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
            final float textSize = context.getResources().getDimension(R.dimen.menu_item_image_height) / 3.0f;
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

    @SuppressWarnings("WeakerAccess")
    public void testEnlarge() {     // TODO: Remove this
        android.view.ViewGroup.LayoutParams lp = gripButton.getLayoutParams();
        lp.width = lp.width * 8 / 7;
        lp.height = lp.height * 8 / 7;
        gripButton.setLayoutParams(lp);
        gripButton.requestLayout();
        lp = exitButton.getLayoutParams();
        lp.width = exitButton.getWidth() * 8 / 7;
        lp.height = exitButton.getHeight() * 8 / 7;
        exitButton.setLayoutParams(lp);
        exitButton.requestLayout();
    }
}
