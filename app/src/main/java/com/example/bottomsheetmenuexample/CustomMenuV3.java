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
import androidx.appcompat.widget.AppCompatButton;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import java.util.Calendar;
import java.util.Date;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;

interface ActivationListener {
    void onActivationListener();
}

class CustomMenuV3 implements ActivationListener {
    private static final int NEW_GAME_BUTTON = R.id.menuButton1;
    private static final int BATTERY_BUTTON = R.id.menuButton3;

    private Context context;
    private DynamicArrowView gripButton;
    private AppCompatButton batteryButton;
    private View exitButton;
    private View persistentMenuView;

    private MenuWithActivationListener menu;

    private static class MenuWithActivationListener extends BottomSheetMenu.WithAnimatedGripButton.WithUpperPanel {
        private int lastBottomSheetState = STATE_COLLAPSED;
        private ActivationListener activationListener;

        MenuWithActivationListener(@NonNull View sheetView, @NonNull View persistentMenuView, @NonNull DynamicArrowView gripButton, @NonNull View upperPanel, @NonNull ActivationListener activationListener) {
            super(sheetView, persistentMenuView, gripButton, upperPanel);
            this.activationListener = activationListener;
        }

        @Override
        public void bottomSheetOnStateChanged(View bottomSheet, int newState) {
            super.bottomSheetOnStateChanged(bottomSheet, newState);
            if (lastBottomSheetState == STATE_COLLAPSED && newState != STATE_COLLAPSED) {
                activationListener.onActivationListener();
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

        menu = new MenuWithActivationListener(
            activityRoot.findViewById(R.id.menuLayout), persistentMenuView, gripButton, exitButton, this);

        menu.setFadeDelay(300);
        menu.setFadedOpacity(0.3f);
        batteryButton = activityRoot.findViewById(BATTERY_BUTTON);
    }

    @Override
    public void onActivationListener() {
        updateBatteryItem();
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

    private final IntentFilter batteryInfoRequest = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
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

            final float[] hsv = {newLevel * 1.2f, 1.0f, 0.9f};
            batteryDrawable.setColorFilter(new PorterDuffColorFilter(Color.HSVToColor(255, hsv), PorterDuff.Mode.SRC_IN));

            if (!newCharging) {
                if (batteryBitmap == null) {
                    batteryBitmap = Bitmap.createBitmap(batteryDrawable.getIntrinsicWidth(), batteryDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    batteryBitmapCanvas = new Canvas(batteryBitmap);
                    batteryTextPaint = new Paint();
                    final float textSize = context.getResources().getDimension(R.dimen.menu_item_image_height) / 3.0f;
                    batteryTextPaint.setTextSize(textSize);
                    batteryTextPaint.setColor(0xFF000000);
                    batteryTextPaint.setAntiAlias(true);
                    batteryTextPaint.setStyle(Paint.Style.FILL);
                    batteryTextPaint.setTextAlign(Paint.Align.CENTER);
                    Rect textBounds = new Rect();
                    final String text = "0123456789%";
                    batteryTextPaint.getTextBounds(text, 0, text.length(), textBounds);
                    batteryDischargingDrawable.setBounds(0, 0, batteryBitmapCanvas.getWidth(), batteryBitmapCanvas.getHeight());
                    batteryTextX = batteryBitmap.getWidth() / 2;
                    batteryTextY = (batteryBitmap.getHeight() + textBounds.height()) / 2;
                    batteryComposedDrawable = new BitmapDrawable(context.getResources(), batteryBitmap);
                }
                else batteryBitmap.eraseColor(0);
                batteryDischargingDrawable.draw(batteryBitmapCanvas);
                batteryTextPaint.setTextScaleX(newLevel == 100? 0.85f: 1.0f);
                batteryBitmapCanvas.drawText(newLevel + "%", batteryTextX, batteryTextY, batteryTextPaint);
                batteryDrawable = batteryComposedDrawable;
            }
            batteryButton.setCompoundDrawablesWithIntrinsicBounds(null, batteryDrawable, null, null);
            batteryLevel = newLevel;
            batteryCharging = newCharging;
            batteryButton.invalidate();
        }
    }

    private void updateBatteryItem()
    {
        final Intent batteryInfo = context.registerReceiver(null, batteryInfoRequest);
        if (batteryInfo == null) return;

        final int level = batteryInfo.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        final int scale = batteryInfo.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        final int status = batteryInfo.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
        final int batteryLevel = (int)((100.0f * level) / scale + 0.5f);
        final boolean isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING);
        updateBatteryItem(getCurrentTime(), batteryLevel, isCharging);
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
