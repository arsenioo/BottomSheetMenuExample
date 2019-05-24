package com.example.bottomsheetmenuexample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;

public class ButtonVectorDrawableCompat extends AppCompatButton {
    public ButtonVectorDrawableCompat(Context context){
        super(context);
    }

    public ButtonVectorDrawableCompat(Context context, AttributeSet attrs){
        super(context,attrs);
        initAttrs(context,attrs);
    }

    public ButtonVectorDrawableCompat(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttrs(context,attrs);
    }



    void initAttrs(Context context, AttributeSet attrs) {
        if(attrs != null){
            TypedArray attributeArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.ButtonVectorDrawableCompat);

            Drawable drawableLeft = null, drawableTop = null, drawableRight = null, drawableBottom = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawableLeft = attributeArray.getDrawable(R.styleable.ButtonVectorDrawableCompat_drawableLeftCompat);
                drawableTop = attributeArray.getDrawable(R.styleable.ButtonVectorDrawableCompat_drawableTopCompat);
                drawableRight = attributeArray.getDrawable(R.styleable.ButtonVectorDrawableCompat_drawableRightCompat);
                drawableBottom = attributeArray.getDrawable(R.styleable.ButtonVectorDrawableCompat_drawableBottomCompat);
            } else {
                int id = attributeArray.getResourceId(R.styleable.ButtonVectorDrawableCompat_drawableLeftCompat, -1);
                if (id != -1) drawableLeft = AppCompatResources.getDrawable(context, id);

                id = attributeArray.getResourceId(R.styleable.ButtonVectorDrawableCompat_drawableTopCompat, -1);
                if (id != -1) drawableTop = AppCompatResources.getDrawable(context, id);

                id = attributeArray.getResourceId(R.styleable.ButtonVectorDrawableCompat_drawableRightCompat, -1);
                if (id != -1) drawableRight = AppCompatResources.getDrawable(context, id);

                id = attributeArray.getResourceId(R.styleable.ButtonVectorDrawableCompat_drawableBottomCompat, -1);
                if (id != -1) drawableBottom = AppCompatResources.getDrawable(context, id);
            }
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
            attributeArray.recycle();
        }
    }
}