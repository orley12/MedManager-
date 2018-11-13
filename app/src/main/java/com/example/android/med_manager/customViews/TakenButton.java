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

public class TakenButton extends AppCompatButton {
    private Drawable mTakenButtonImage;

    public TakenButton(Context context) {
        super(context);
        init();
    }

    public TakenButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TakenButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTakenButtonImage = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_small_capsule_for_card_i, null);
        showClearButton();
    }

        private void showClearButton() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                setCompoundDrawablesRelativeWithIntrinsicBounds
                        (mTakenButtonImage,                      // Start of text.
                                null,               // Above text.
                                null,  // End of text.
                                null);              // Below text.
            }
        }
    }
