package com.example.bottomsheetmenuexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
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

class SoftickMenu implements MenuActivationListener {
    private static final int BATTERY_UPDATE_FREQ = 5000;
    private static final int FADE_DELAY = 300;
    private static final float FADED_OPACITY = 0.3f;
    private Context context;
    private AppCompatButton batteryButton;
    private View exitButton;
    private View persistentMenuView;

    private BottomSheetMenu.WithAnimatedGripButton.WithUpperPanel menu;

    private final Runnable batteryInfoUpdater = new Runnable() {
        private final IntentFilter batteryInfoRequest = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        @Override
        public void run() {
            updateBatteryItem(context.registerReceiver(null, batteryInfoRequest));
            batteryButton.postDelayed(batteryInfoUpdater, BATTERY_UPDATE_FREQ);
        }
    };

    SoftickMenu(@NonNull Context context, @NonNull ViewGroup activityRoot, @NonNull View persistentMenuView)
    {
        this.context = context;
        this.persistentMenuView = persistentMenuView;
        exitButton = activityRoot.findViewById(R.id.exitButton);
        final DynamicArrowView gripButton = activityRoot.findViewById(R.id.gripButton);

        menu = new BottomSheetMenu.WithAnimatedGripButton.WithUpperPanel(
            activityRoot.findViewById(R.id.menuLayout), persistentMenuView, gripButton, exitButton);

        menu.setActivationListener(this);

        // Some hardcoded appearance
        menu.setFadeDelay(FADE_DELAY);
        menu.setFadedOpacity(FADED_OPACITY);
        batteryButton = activityRoot.findViewById(R.id.batteryButton);
    }

    @Override
    public void onActivated() {
        batteryInfoUpdater.run();
    }

    @Override
    public void onCollapsed() {
        batteryButton.removeCallbacks(batteryInfoUpdater);
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

    private String getCurrentTime()
    {
        final Date time = Calendar.getInstance().getTime();
        return DateFormat.getTimeFormat(context).format(time);
    }

    private int batteryLevel = -1;
    private boolean batteryCharging;
    private String batteryIconCaption;
    private Paint batteryTextPaint;
    private Drawable batteryChargingDrawable;
    private Drawable batteryDischargingDrawable;
    private Drawable batteryComposedDrawable;
    private Bitmap batteryBitmap;
    private Canvas batteryBitmapCanvas;
    private int batteryTextX;
    private int batteryTextY;

    private void updateBatteryItem(String newCaption, int newLevel, boolean newCharging) {
        if (!newCaption.equals(batteryIconCaption)) {
            batteryButton.setText(newCaption);
            batteryIconCaption = newCaption;
            batteryButton.invalidate();
        }

        if (newLevel != batteryLevel || newCharging != batteryCharging) {
            // Lazy resources loading
            Drawable batteryDrawable;
            if (newCharging) {
                if (batteryChargingDrawable == null) {
                    batteryChargingDrawable = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_menu_battery_charge, context.getTheme());
                }
                batteryDrawable = batteryChargingDrawable;
            } else {
                if (batteryDischargingDrawable == null) {
                    batteryDischargingDrawable = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_menu_battery_base, context.getTheme());
                }
                batteryDrawable = batteryDischargingDrawable;
            }
            if (batteryDrawable == null) return;

            final float[] hsv = {newLevel * 1.2f, 1.0f, 0.9f};  // Battery color from red to green
            batteryDrawable.setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(255, hsv), PorterDuff.Mode.SRC_IN));

            if (!newCharging) {
                if (batteryBitmap == null) {
                    // Lazy resources loading, part 2
                    batteryBitmap = Bitmap.createBitmap(batteryDrawable.getIntrinsicWidth(), batteryDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    batteryBitmapCanvas = new Canvas(batteryBitmap);
                    batteryTextPaint = new Paint();

                    final String text = "0";
                    final float lineHeight = batteryBitmapCanvas.getHeight() * 15.0f / 30.0f * 0.5f;    // Desired height according to drawing dimensions
                    final float initialTextSize = 24.0f;                                                // Initial text size, any value
                    batteryTextPaint.setTextSize(initialTextSize);
                    Rect textBounds = new Rect();
                    batteryTextPaint.getTextBounds(text, 0, 1, textBounds);
                    batteryTextPaint.setTextSize(initialTextSize * lineHeight / textBounds.height());  // Final size according to measurements
                    batteryTextPaint.getTextBounds(text, 0, 1, textBounds);

                    batteryTextPaint.setColor(0xFF000000);
                    batteryTextPaint.setAntiAlias(true);
                    batteryTextPaint.setStyle(Paint.Style.FILL);
                    batteryTextPaint.setTextAlign(Paint.Align.CENTER);
                    batteryDischargingDrawable.setBounds(0, 0, batteryBitmapCanvas.getWidth(), batteryBitmapCanvas.getHeight());
                    batteryTextX = batteryBitmap.getWidth() / 2;
                    batteryTextY = (batteryBitmap.getHeight() + textBounds.height()) / 2;
                    batteryComposedDrawable = new BitmapDrawable(context.getResources(), batteryBitmap);
                }
                else batteryBitmap.eraseColor(0);
                batteryDischargingDrawable.draw(batteryBitmapCanvas);
                batteryTextPaint.setTextScaleX(newLevel == 100? 0.75f: 0.95f);
                batteryBitmapCanvas.drawText(newLevel + "%", batteryTextX, batteryTextY, batteryTextPaint);
                batteryDrawable = batteryComposedDrawable;
            }
            batteryButton.setCompoundDrawablesWithIntrinsicBounds(null, batteryDrawable, null, null);
            batteryLevel = newLevel;
            batteryCharging = newCharging;
            batteryButton.invalidate();
        }
    }

    private void updateBatteryItem(@Nullable Intent batteryInfo) {
        if (batteryInfo == null) return;
        final int level = batteryInfo.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final int scale = batteryInfo.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        final int status = batteryInfo.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
        final int batteryLevel = (int)((100.0f * level) / scale + 0.5f);
        final boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING);
        updateBatteryItem(getCurrentTime(), batteryLevel, isCharging);
    }
}
