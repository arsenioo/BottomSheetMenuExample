package com.example.bottomsheetmenuexample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.flexbox.FlexboxLayout;

public class MainActivity extends AppCompatActivity {

    ViewGroup rootLayout;
    CustomMenuV3 mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(R.id.rootLayout);
        final FlexboxLayout persistentMenuView = rootLayout.findViewById(R.id.buttonsFlex);

        final View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                if (left == oldLeft && right == oldRight && top == oldTop && bottom == oldBottom) return;

                final boolean newWrap = rootLayout.getWidth() <= rootLayout.getHeight();
                final View viewToWrap = persistentMenuView.getChildAt((persistentMenuView.getChildCount() + 1) / 2);
                final FlexboxLayout.LayoutParams lp = (FlexboxLayout.LayoutParams)viewToWrap.getLayoutParams();
                if (lp.isWrapBefore() == newWrap) return;
                lp.setWrapBefore(newWrap);
                viewToWrap.setLayoutParams(lp);
                viewToWrap.requestLayout();
            }
        };

        rootLayout.addOnLayoutChangeListener(layoutChangeListener);

        // Create menu, initialize with defaults
        mMenu = new CustomMenuV3(this, rootLayout, persistentMenuView);
        mMenu.setGripButtonEnabled(true);        // Synchronize with xml layout initial state
        mMenu.setLeftHandled(true);
    }

    @Override
    public void onBackPressed() {
        if (mMenu.isActive()) mMenu.hide();
        else super.onBackPressed();
    }

    public void onFirstButtonClick(View v)   {mMenu.hide(); Toast.makeText(this, "NewGame", Toast.LENGTH_SHORT).show();}
    public void onSecondButtonClick(View v)  {Toast.makeText(this, "Undo", Toast.LENGTH_SHORT).show();}
    public void onThirdButtonClick(View v)   {mMenu.hide(); Toast.makeText(this, "Hints", Toast.LENGTH_SHORT).show();}

    public void onFourthButtonClick(View v)  {
        mMenu.setLeftHandled(true);
        mMenu.setGripButtonEnabled(true);
        Toast.makeText(this, "Options", Toast.LENGTH_SHORT).show();
    }

    public void onFifthButtonClick(View v)   {mMenu.testEnlarge(); mMenu.hide(); Toast.makeText(this, "Solution", Toast.LENGTH_SHORT).show();}

    public void onSixthButtonClick(View v) {
        mMenu.setLeftHandled(false);
        mMenu.setGripButtonEnabled(false);
        mMenu.hide(); Toast.makeText(this, "Progress", Toast.LENGTH_SHORT).show();
    }
    public void onSeventhButtonClick(View v) {mMenu.hide(); Toast.makeText(this, "Debug", Toast.LENGTH_SHORT).show();}

    public void onExitButtonClick(View v)
    {
        finish();
    }
}
