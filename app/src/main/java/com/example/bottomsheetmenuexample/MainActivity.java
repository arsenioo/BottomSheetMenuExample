package com.example.bottomsheetmenuexample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    ViewGroup rootLayout;
    CustomMenuV3 mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(android.R.id.content);
        createMenu();
    }

    private void createMenu()
    {
        if (mMenu == null)
        {
            mMenu = new CustomMenuV3(this, rootLayout, false, true);
        }
    }

    public void onFirstButtonClick(View v) {Toast.makeText(this, "NewGame", Toast.LENGTH_LONG).show();}
    public void onSecondButtonClick(View v){Toast.makeText(this, "Undo", Toast.LENGTH_LONG).show();}
    public void onThirdButtonClick(View v){Toast.makeText(this, "Hints", Toast.LENGTH_LONG).show();}
    public void onFourthButtonClick(View v){Toast.makeText(this, "Options", Toast.LENGTH_LONG).show();}
    public void onFifthButtonClick(View v){Toast.makeText(this, "Solution", Toast.LENGTH_LONG).show();}
    public void onSixthButtonClick(View v){Toast.makeText(this, "Progress", Toast.LENGTH_LONG).show();}
    public void onSeventhButtonClick(View v){Toast.makeText(this, "Debug", Toast.LENGTH_LONG).show();}
    public void onExitButtonClick(View v)
    {
    }
}
