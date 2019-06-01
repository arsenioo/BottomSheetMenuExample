package com.example.bottomsheetmenuexample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SampleActivity extends AppCompatActivity {

    ViewGroup rootLayout;
    SoftickMenu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(R.id.rootLayout);

        // Create menu, initialize with defaults
        mMenu = new SoftickMenu(this, rootLayout, rootLayout.findViewById(R.id.buttonsFlex));
        mMenu.setGripButtonEnabled(true);        // Synchronize with xml layout initial state
        mMenu.setLeftHandled(true);
    }

    @Override
    public void onBackPressed() {
        if (mMenu.isActive()) mMenu.hide();
        else super.onBackPressed();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMenu.hide();
    }

    public void onFirstButtonClick(View v)   {mMenu.hide(); Toast.makeText(this, "NewGame", Toast.LENGTH_SHORT).show();}
    public void onSecondButtonClick(View v)  {Toast.makeText(this, "Undo", Toast.LENGTH_SHORT).show();}
    public void onThirdButtonClick(View v)   {mMenu.hide(); Toast.makeText(this, "Hints", Toast.LENGTH_SHORT).show();}

    public void onFourthButtonClick(View v)  {
        mMenu.setLeftHandled(true);
        mMenu.setGripButtonEnabled(true);
        Toast.makeText(this, "Options", Toast.LENGTH_SHORT).show();
    }

    public void onFifthButtonClick(View v)   {mMenu.hide(); Toast.makeText(this, "Solution", Toast.LENGTH_SHORT).show();}

    public void onSixthButtonClick(View v) {
        mMenu.setLeftHandled(false);
        mMenu.setGripButtonEnabled(false);
        mMenu.hide(); Toast.makeText(this, "Progress", Toast.LENGTH_SHORT).show();
    }

    public void onExitButtonClick(View v)
    {
        finish();
    }
}
