package com.vip.android.viptechnician.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Android on 7/18/2017.
 */

public class MonteserattEditText extends androidx.appcompat.widget.AppCompatEditText
{
    public MonteserattEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MonteserattEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonteserattEditText(Context context) {
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
