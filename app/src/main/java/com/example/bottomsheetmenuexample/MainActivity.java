package com.example.bottomsheetmenuexample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ViewGroup rootLayout;
    CustomMenuV3 mMenu;
    DynamicArrowView testArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_main);
        rootLayout = findViewById(android.R.id.content);
        testArrow = new DynamicArrowView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(200, 200, Gravity.CENTER);
        testArrow.setBackgroundColor(0xFF808080);
        testArrow.setInnerColor(0xFFFF0000);
        testArrow.setInnerWidth(20.0f);
        testArrow.setOuterColor(0xFFFFFFFF);
        testArrow.setOuterWidth(30.0f);
        testArrow.setArrowPhase(1f);
        rootLayout.addView(testArrow, lp);
        createMenu();
    }

    private void createMenu()
    {
        if (mMenu == null)
        {
            mMenu = new CustomMenuV3(this, rootLayout, true, true);
            rootLayout.addView(mMenu);
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
        testArrow.setArrowPhase((SystemClock.currentThreadTimeMillis() % 20) / 19.0f);
        testArrow.invalidate();
    }

    class DynamicArrowView extends View {
        private Paint arrowPaint = new Paint();
        private Path arrowPath = new Path();
        private float innerWidth;
        private float outerWidth;
        private float arrowPhase;
        private int innerColor;
        private int outerColor;

        public DynamicArrowView(Context context) {
            super(context);
            arrowPaint.setStyle(Paint.Style.STROKE);
            arrowPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        void setInnerWidth(float width)
        {
            innerWidth = width;
        }

        void setOuterWidth(float width)
        {
            outerWidth = width;
        }

        void setArrowPhase(float phase)
        {
            arrowPhase = phase;
            recalculatePath();
        }

        void setInnerColor(int color)
        {
            innerColor = color;
        }

        void setOuterColor(int color)
        {
            outerColor = color;
        }

        void recalculatePath()
        {
            final float sizeX = getWidth() / 2.0f;
            final float sizeY = getHeight() / 4.0f;

            final float y1 = (getHeight() - sizeY) / 2.0f;
            final float x1 = (getWidth() - sizeX) / 2.0f;
            final float x2 = x1 + sizeX / 2.0f;
            final float x3 = x1 + sizeX;
            final float phase = (float) Math.sin(Math.PI / 2.0f * arrowPhase); //for better "bird" rotation simulation
            final float yEnds = y1 + sizeY * (1 - phase);
            final float yCenter = y1 + sizeY * phase;
            arrowPath.rewind();
            arrowPath.moveTo(x1, yEnds);
            arrowPath.lineTo(x2, yCenter);
            arrowPath.lineTo(x3, yEnds);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            recalculatePath();
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            if (outerColor != 0) {
                arrowPaint.setColor(outerColor);
                arrowPaint.setStrokeWidth(outerWidth);
                canvas.drawPath(arrowPath, arrowPaint);
            }

            if (innerColor != 0) {
                arrowPaint.setColor(innerColor);
                arrowPaint.setStrokeWidth(innerWidth);
                canvas.drawPath(arrowPath, arrowPaint);
            }
        }
    }
}
