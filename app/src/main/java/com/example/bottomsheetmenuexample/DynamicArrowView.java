package com.example.bottomsheetmenuexample;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;

class DynamicArrowView extends View {
    private Paint arrowPaint = new Paint();
    private Path arrowPath = new Path();
    private float innerWidth;
    private float outerWidth;
    private float arrowPhase;
    private int innerColor;
    private int outerColor;
    private float opacity = 0;
    private Drawable selectableBackground;
    private Drawable backgroundDrawable;

    public DynamicArrowView(Context context) {
        this(context, null, 0);
    }

    public DynamicArrowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicArrowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        arrowPaint.setAntiAlias(true);
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setStrokeCap(Paint.Cap.ROUND);
        if (attrs == null) return;

        try {
            TypedArray attributeArray;
            attributeArray = context.obtainStyledAttributes(attrs, R.styleable.DynamicArrowView);
            setInnerWidth(attributeArray.getDimension(R.styleable.DynamicArrowView_innerWidth, 2f));
            setOuterScaleFactor(attributeArray.getFloat(R.styleable.DynamicArrowView_outerScaleFactor, 2f));
            setInnerColor(attributeArray.getColor(R.styleable.DynamicArrowView_innerColor, Color.RED));
            setOuterColor(attributeArray.getColor(R.styleable.DynamicArrowView_outerColor, Color.GREEN));

            int id = attributeArray.getResourceId(R.styleable.DynamicArrowView_selectableBackground, -1);
            if (id != -1) {
                selectableBackground = ResourcesCompat.getDrawable(getResources(), id, context.getTheme());
            }
            id = attributeArray.getResourceId(R.styleable.DynamicArrowView_backgroundDrawable, -1);
            if (id != -1) {
                backgroundDrawable = ResourcesCompat.getDrawable(getResources(), id, context.getTheme());
            }

            constructBackground();
            attributeArray.recycle();
        } catch (Exception e) {
            Log.e(this.toString(), "Attributes initialization error", e);
            throw e;
        }
        recalculatePath();
    }

    public void setInnerWidth(float width) {
        innerWidth = width;
    }

    public void setOuterScaleFactor(float scaleFactor) {
        outerWidth = innerWidth * scaleFactor;
    }

    public void setArrowPhase(float phase) {
        if (arrowPhase == phase) return;
        arrowPhase = phase;
        recalculatePath();
    }

    public void setInnerColor(int color) {
        innerColor = color;
    }

    public void setOuterColor(int color) {
        outerColor = color;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    private final ValueAnimator.AnimatorUpdateListener animationListener = new ValueAnimator.AnimatorUpdateListener()
    {
        public void onAnimationUpdate(ValueAnimator animation) {
            setOpacity((float)animation.getAnimatedValue());
            invalidate();
        }
    };

    public void animateOpacity(float newOpacity)
    {
        if (opacity == newOpacity) return;
        ValueAnimator va = ValueAnimator.ofFloat(opacity, newOpacity);
        va.setDuration(600);
        va.addUpdateListener(animationListener);
        va.start();
    }

    @SuppressWarnings("unused")
    public void setSelectableBackground(Drawable drawable) {
        selectableBackground = drawable;
        constructBackground();
    }

    @SuppressWarnings("unused")
    public void setDrawable(Drawable drawable) {
        backgroundDrawable = drawable;
        constructBackground();
    }

    private void constructBackground() {
        if (selectableBackground != null) {
            if (backgroundDrawable != null) {
                Drawable[] layers = {backgroundDrawable, selectableBackground};
                setBackgroundDrawable(new LayerDrawable(layers).mutate());      // Both, using layers
            } else setBackgroundDrawable(selectableBackground);                 // Selectable only
        } else setBackgroundDrawable(backgroundDrawable);                       // Background only or null
    }

    private void recalculatePath()
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

    private void drawArrowPart(Canvas canvas, float width, int color) {
        arrowPaint.setColor(ColorUtils.setAlphaComponent(color, (int)(255 * opacity)));
        arrowPaint.setStrokeWidth(width);
        canvas.drawPath(arrowPath, arrowPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (outerColor != 0) drawArrowPart(canvas, outerWidth, outerColor);
        if (innerColor != 0) drawArrowPart(canvas, innerWidth, innerColor);
    }
}
