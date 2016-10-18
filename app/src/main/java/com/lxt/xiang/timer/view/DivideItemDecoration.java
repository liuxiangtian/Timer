package com.lxt.xiang.timer.view;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DivideItemDecoration extends RecyclerView.ItemDecoration{

    private int width;
    private boolean isVertical;
    private Paint mPaint;

    public DivideItemDecoration(int width) {
        this.width = width;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GRAY);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0,0,0,width);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getLeft();
        int right = parent.getRight();
        int count = parent.getChildCount();
        for (int i = 0; i < count -1; i++) {
            View child = parent.getChildAt(i);
            int top = child.getBottom();
            int bottom = top+width;
            c.drawRect(left,top, right, bottom, mPaint);
        }
    }

}
