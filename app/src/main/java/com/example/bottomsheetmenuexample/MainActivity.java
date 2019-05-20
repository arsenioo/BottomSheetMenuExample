package com.example.bottomsheetmenuexample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    ViewGroup rootLayout;
    CustomMenuV3 mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(android.R.id.content);
        createMenu();
    }

    private void createMenu()
    {
        if (mMenu == null)
        {
            mMenu = new CustomMenuV3(this, rootLayout);
            rootLayout.addView(mMenu);
        }
    }

    public void onFirstButtonClick(View v){}
    public void onSecondButtonClick(View v){}
    public void onThirdButtonClick(View v){}
    public void onFourthButtonClick(View v){}
    public void onFifthButtonClick(View v){}
    public void onSixthButtonClick(View v){}
    public void onSeventhButtonClick(View v){}
    public void onExitButtonClick(View v)
    {
        finish();
    }

}
