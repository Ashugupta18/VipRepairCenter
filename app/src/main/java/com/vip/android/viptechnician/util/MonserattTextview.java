package com.vip.android.viptechnician.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Android on 7/17/2017.
 */

public class MonserattTextview extends androidx.appcompat.widget.AppCompatTextView
{
    public MonserattTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MonserattTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonserattTextview(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/MontserratLight.otf");
            setTypeface(tf);
        }
    }
}
