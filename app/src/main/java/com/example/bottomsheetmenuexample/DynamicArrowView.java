package com.example.bottomsheetmenuexample;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import androidx.core.graphics.ColorUtils;

class DynamicArrowView extends View {
    private Paint arrowPaint = new Paint();
    private Path arrowPath = new Path();
    private float innerWidth;
    private float outerWidth;
    private float arrowPhase;
    private int innerColor;
    private int outerColor;
    private float alpha = 0;

    public DynamicArrowView(Context context) {
        super(context);
        arrowPaint.setAntiAlias(true);
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
            arrowPaint.setColor(ColorUtils.setAlphaComponent(outerColor, (int)(255 * alpha)));
            arrowPaint.setStrokeWidth(outerWidth);
            canvas.drawPath(arrowPath, arrowPaint);
        }

        if (innerColor != 0) {
            arrowPaint.setColor(ColorUtils.setAlphaComponent(innerColor, (int)(255 * alpha)));
            arrowPaint.setStrokeWidth(innerWidth);
            canvas.drawPath(arrowPath, arrowPaint);
        }
    }

    final ValueAnimator.AnimatorUpdateListener animationListener = new ValueAnimator.AnimatorUpdateListener()
    {
        public void onAnimationUpdate(ValueAnimator animation) {
            alpha = (float)animation.getAnimatedValue();
            invalidate();
        }
    };

    public void animateAlpha(float newAlpha)
    {
        if (alpha == newAlpha) return;
        ValueAnimator va = ValueAnimator.ofFloat(alpha, newAlpha);
        va.setDuration(300);
        va.addUpdateListener(animationListener);
        va.start();
    }
}
