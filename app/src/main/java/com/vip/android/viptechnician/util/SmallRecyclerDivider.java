package com.vip.android.viptechnician.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.vip.android.viptechnician.R;


/**
 * Created by Android on 4/3/2017.
 */

public class SmallRecyclerDivider extends RecyclerView.ItemDecoration
{
    private Drawable mDivider;
    int left,right,childCount,top,bottom;
    View child;
    int i;
    RecyclerView.LayoutParams params;

    public SmallRecyclerDivider(Context context)
    {
        mDivider = context.getResources().getDrawable(R.drawable.small_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
    {

        left = parent.getPaddingLeft();
        right = parent.getWidth() - parent.getPaddingRight();

        childCount = parent.getChildCount();
        i=0;
        for (i = 0; i < childCount-1; i++)
        {
            child = null;
            child = parent.getChildAt(i);

            params = null;
            params = (RecyclerView.LayoutParams) child.getLayoutParams();

            top = child.getBottom() + params.bottomMargin;
            bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            return;
        }

        outRect.top = mDivider.getIntrinsicHeight();
    }
}
