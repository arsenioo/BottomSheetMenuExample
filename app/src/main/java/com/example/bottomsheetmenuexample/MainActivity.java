package com.example.bottomsheetmenuexample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;

public class MainActivity extends AppCompatActivity {

    ViewGroup rootLayout;
    AsyncLayoutInflater mLayoutInflater;
    BottomSheetMenu mMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(android.R.id.content);
        mLayoutInflater = new AsyncLayoutInflater(this);
        createMenu();
    }

    private void createMenu()
    {
        mMenu = new BottomSheetMenu(this);
        rootLayout.addView(mMenu);
        final AsyncLayoutInflater.OnInflateFinishedListener topViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup parent)
            {
                mMenu.setTopView(view);
            }
        };
        final AsyncLayoutInflater.OnInflateFinishedListener bottomViewCallback = new AsyncLayoutInflater.OnInflateFinishedListener()
        {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup parent)
            {
                mMenu.setBottomView(view);
            }
        };
        mLayoutInflater.inflate(R.layout.menu_top_part, null, topViewCallback);
        mLayoutInflater.inflate(R.layout.menu_bottom_part, null, bottomViewCallback);
    }

    public void onFirstButtonClick(View v){}
    public void onSecondButtonClick(View v){}
    public void onThirdButtonClick(View v){}
    public void onFourthButtonClick(View v){}
    public void onFifthButtonClick(View v){}
    public void onSixthButtonClick(View v){}
    public void onSeventhButtonClick(View v){}

}
