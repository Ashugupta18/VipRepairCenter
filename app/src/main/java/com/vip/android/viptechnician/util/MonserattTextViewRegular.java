package com.vip.android.viptechnician.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by Android on 7/21/2017.
 */

public class MonserattTextViewRegular extends androidx.appcompat.widget.AppCompatTextView
{
    public MonserattTextViewRegular(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MonserattTextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonserattTextViewRegular(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/MontserratRegular.otf");
            setTypeface(tf);
        }
    }
}
