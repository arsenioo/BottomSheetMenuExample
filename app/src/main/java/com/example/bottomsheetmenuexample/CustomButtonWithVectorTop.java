package com.example.bottomsheetmenuexample;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;

public class CustomButtonWithVectorTop extends AppCompatButton {
    public CustomButtonWithVectorTop(Context context){
        super(context);
    }

    public CustomButtonWithVectorTop(Context context, AttributeSet attrs){
        super(context,attrs);
        initAttrs(context,attrs);
    }

    public CustomButtonWithVectorTop(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
        initAttrs(context,attrs);
    }



    void initAttrs(Context context, AttributeSet attrs) {
        if(attrs != null){
            TypedArray attributeArray = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.CustomButtonWithVectorTop);

            Drawable drawableTop = null;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                drawableTop = attributeArray.getDrawable(R.styleable.CustomButtonWithVectorTop_drawableTopCompat);
            } else {
                final int drawableTopId =attributeArray.getResourceId(R.styleable.CustomButtonWithVectorTop_drawableTopCompat, -1);

                if(drawableTopId != -1)
                    drawableTop = AppCompatResources.getDrawable(context,drawableTopId);
            }
            setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
            attributeArray.recycle();
        }
    }
}