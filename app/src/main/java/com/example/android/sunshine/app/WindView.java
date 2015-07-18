package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/**
 * Created by Jeffery on 7/14/2015.
 */
public class WindView extends View {

    private float mWindSpeed = 0;
    private float mWindDirection = 0;
    private Context mContext;
    private Path mArrow;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WindView(Context context) {
        super(context);
        mContext = context;
    }

    public WindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public WindView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    private void createArrowPath(int width, int height){
        int squareSize = 0;
        if(width > height)
            squareSize = height;
        else squareSize = width;
        squareSize -= 10;
        mArrow = new Path();
        mArrow.setFillType(Path.FillType.EVEN_ODD);
        mArrow.moveTo(width / 2, height / 2);
        mArrow.rMoveTo(0, -squareSize / 2);
        mArrow.rLineTo(-squareSize / 4, squareSize / 4);
        mArrow.rLineTo(squareSize / 8, 0);
        mArrow.rLineTo(0, squareSize / 2);
        mArrow.rLineTo(squareSize / 4, 0);
        mArrow.rLineTo(0, -squareSize / 2);
        mArrow.rLineTo(squareSize / 8, 0);
        mArrow.rLineTo(-squareSize / 4, -squareSize / 4);
        mArrow.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();


        canvas.rotate(mWindDirection, width / 2, height / 2);

        mPaint.setColor(mContext.getResources().getColor(R.color.sunshine_blue));
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawPath(mArrow, mPaint);
        mPaint.setColor(mContext.getResources().getColor(R.color.sunshine_dark_blue));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        canvas.drawPath(mArrow,mPaint);
    }

    public void setWindSpeed(float windSpeed){
        mWindSpeed = windSpeed;
    }

    public void setWindDirection(float windDir){
        mWindDirection = windDir;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 100;
        int desiredHeight = 100;

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if(wSpecMode == MeasureSpec.EXACTLY)
            width = wSpecSize;
        else if (wSpecMode == MeasureSpec.AT_MOST)
            width = Math.min(wSpecSize,desiredWidth);
        else width = desiredWidth;

        if(hSpecMode == MeasureSpec.EXACTLY)
            height = hSpecSize;
        else if (hSpecMode == MeasureSpec.AT_MOST)
            height = Math.min(hSpecSize,desiredHeight);
        else height = desiredHeight;

        createArrowPath(width, height);
        setMeasuredDimension(width,height);
    }

}
