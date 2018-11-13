package com.example.android.med_manager.customViews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.example.android.med_manager.R;

/**
 * Created by SOLARIN O. OLUBAYODE on 18/06/18.
 */

public class IgnoreButton extends AppCompatButton {
    private Drawable mIgnoreButtonImage;

    public IgnoreButton(Context context) {
        super(context);
        init();
    }

    public IgnoreButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IgnoreButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mIgnoreButtonImage = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_ignore_ii, null);
        showButtonImage();
    }

    private void showButtonImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setCompoundDrawablesRelativeWithIntrinsicBounds
                    (mIgnoreButtonImage,                      // Start of text.
                            null,               // Above text.
                            null,  // End of text.
                            null);              // Below text.
        }
    }
}
