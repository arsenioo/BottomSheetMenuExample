package com.example.bottomsheetmenuexample;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Calendar;
import java.util.Date;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@SuppressLint("ViewConstructor")
public class CustomMenuV3 extends BottomSheetMenu implements View.OnClickListener
{
    private static final int NEW_GAME_BUTTON_TEXT = R.id.first_menu_item_caption;

    private static final int BATTERY_BUTTON_ICON = R.id.third_menu_item_icon;
    private static final int BATTERY_BUTTON_TEXT = R.id.third_menu_item_caption;
    View menuControlButtonFrame;
    private DynamicArrowView menuControlButton;
    private View exitButton;
    private boolean _menuButtonEnabled;
    private View topView;
    private View bottomView;
    private  ViewGroup parent;
    private int mMenuButtonHeight;
    private int mMenuButtonWidth;
    private boolean isLefthandled;
    private Context mContext;


    @SuppressLint("InflateParams")
    public CustomMenuV3(final Context context, ViewGroup parentView, boolean leftHandled, boolean menuButtonEnabled)
    {
        super(context);
        mContext = context;
        parent = parentView;
        isLefthandled = leftHandled;
        _menuButtonEnabled = menuButtonEnabled;

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
        parentView.addView(this, lp);

        AsyncLayoutInflater mLayoutInflater = new AsyncLayoutInflater(context);
        final AsyncLayoutInflater.OnInflateFinishedListener exitButtonViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(@NonNull View view, int resid, ViewGroup parent)
            {
                exitButton = view;
                parent.addView(exitButton);
                drawExitButton(0);
            }
        };

        final AsyncLayoutInflater.OnInflateFinishedListener topViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(@NonNull View view, int resid, ViewGroup parent)
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
            public void onInflateFinished(@NonNull View view, int resid, ViewGroup p)
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
        menuControlButtonFrame = topView.findViewById(R.id.closeButFrame);
        updateMenuButtonVisibility();
        menuControlButtonFrame.setOnClickListener(this);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)menuControlButtonFrame.getLayoutParams();
        mMenuButtonHeight = params.height;
        mMenuButtonWidth = params.width = (int)(mMenuButtonHeight * 1.2);
        menuControlButtonFrame.setLayoutParams(params);
        menuControlButton = new DynamicArrowView(mContext);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        menuControlButton.setBackgroundColor(mContext.getResources().getColor(R.color.menuBackgroundColor));
        menuControlButton.setInnerColor(Color.WHITE);
        menuControlButton.setInnerWidth(4.0f);
        menuControlButton.setOuterColor(Color.GRAY);
        menuControlButton.setOuterWidth(8.0f);
        menuControlButton.setArrowPhase(1f);
        ((ViewGroup)menuControlButtonFrame).addView(menuControlButton, lp);
        super.invalidate();
        fadeButtonIn();
        fadeButtonOutWithDelay();
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
        menuControlButton.setArrowPhase(slideOffset);
        menuControlButton.invalidate();
    }

    @SuppressLint("RtlHardcoded")
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
        else if(newState != BottomSheetBehavior.STATE_COLLAPSED)
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


    final Runnable fadeButtonOut = new Runnable() {
        @Override
        public void run() {
            menuControlButton.animateAlpha(0.2f);
        }
    };

    private void fadeButtonIn() {
        menuControlButton.animateAlpha(1.0f);
    }

    private void fadeButtonOutWithDelay() {
        removeCallbacks(fadeButtonOut);
        postDelayed(fadeButtonOut, /*D.FADEOUT_MENU_BUTTON_TIMEOUT * 1000*/ 4000);
    }

    @Override
    public void onClick(View v)
    {
        if (v.equals(menuControlButtonFrame)) toggle();
    }


    final Rect menuRect = new Rect();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        // Filter initial touch event only
        if (event.getAction() != MotionEvent.ACTION_DOWN) return false;

        final int tapX = Math.round(event.getX());
        final int tapY = Math.round(event.getY());

        bottomView.getGlobalVisibleRect(menuRect);

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

    public void setLeftHandled(boolean leftHandled)
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
        menuControlButtonFrame.setVisibility(_menuButtonEnabled?View.VISIBLE:View.GONE);
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
}
