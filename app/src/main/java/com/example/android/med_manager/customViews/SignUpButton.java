package com.example.android.med_manager.customViews;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import com.example.android.med_manager.R;

/**
 * Created by SOLARIN O. OLUBAYODE on 28/07/18.
 */

public class SignUpButton extends android.support.v7.widget.AppCompatButton {

    private Drawable mLoginButtonImage;

    public SignUpButton(Context context) {
        super(context);
        init();
    }

    public SignUpButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SignUpButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mLoginButtonImage = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_mail_icon_i, null);
        showButtonImage();
    }

    private void showButtonImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setCompoundDrawablesRelativeWithIntrinsicBounds
                    (mLoginButtonImage,                      // Start of text.
                            null,               // Above text.
                            null,  // End of text.
                            null);              // Below text.
        }
    }

    @Override
    public int getCompoundPaddingLeft() {
        return super.getCompoundPaddingLeft() + 100;
    }

    @Override
    public int getCompoundPaddingRight() {
        return super.getCompoundPaddingRight() + 20;
    }
}
