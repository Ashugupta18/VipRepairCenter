package com.vip.android.viptechnician.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Android on 7/18/2017.
 */

public class ColumnsUtility
{
    public static int calculateNoOfColumns(Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
