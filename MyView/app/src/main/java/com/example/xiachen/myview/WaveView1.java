package com.example.xiachen.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.util.Random;

/**
 * Created by xiachen on 15/11/4.
 */
public class WaveView1 extends View {

    private static final int WAVE_PAINT_COLOR = 0x880000aa;
    private static final float STRETCH_FACTOR_A = 20;
    private static final int OFFSET_Y = 0;
    private static final int WAVE_ONE_SPEED = 7;
    private static final int WAVE_TWO_SPEED = 5;

    private float mCycleFactorW;
    private int mTotalWidth, mTotalHeight;
    private float[] mYPositions;
    private float[] mResetOneYPositions;
    private float[] mResetTwoYPositions;
    private int mXOffsetSpeedOne;
    private int mXOffsetSpeedTwo;
    private int mXOneOffset;
    private int mXTwoOffset;

    private Paint mWaveOnePaint, mWaveTwoPaint;
    private DrawFilter mDrawFilter;

    public WaveView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        mXOffsetSpeedOne = dipToPx(context, WAVE_ONE_SPEED);
        mXOffsetSpeedTwo = dipToPx(context, WAVE_TWO_SPEED);

        mWaveOnePaint = new Paint();
        mWaveOnePaint.setAntiAlias(true);
        mWaveOnePaint.setStyle(Paint.Style.FILL);
        mWaveOnePaint.setColor(WAVE_PAINT_COLOR);

        mWaveTwoPaint = new Paint();
        mWaveTwoPaint.setAntiAlias(true);
        mWaveTwoPaint.setStyle(Paint.Style.FILL);
        mWaveTwoPaint.setColor(Color.argb(150, 0, 153, 255));
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);


    }

    private int dipToPx(Context context, int waveTransSpeed) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return (int) (waveTransSpeed * metrics.density + 0.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        mXOneOffset = 0;
        mResetOneYPositions = new float[mTotalHeight];
        mResetTwoYPositions = new float[mTotalHeight];
        mYPositions = new float[mTotalWidth];
        mCycleFactorW = ((float) (2 * Math.PI / mTotalWidth));
        for (int i = 0; i < mTotalWidth; i++) {
            mYPositions[i] = (float) (STRETCH_FACTOR_A * Math.sin(mCycleFactorW * i) + OFFSET_Y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);
        resetPositionY();
        for (int i = 0; i < mTotalWidth; i++) {
            canvas.drawLine(i, mTotalHeight - mResetOneYPositions[i] - 400, i, mTotalHeight, mWaveOnePaint);
            canvas.drawLine(i, mTotalHeight - mResetTwoYPositions[i] - 400, i, mTotalHeight, mWaveTwoPaint);
        }

        mXOneOffset += mXOffsetSpeedOne;
        mXTwoOffset += mXOffsetSpeedTwo;
        if (mXOneOffset >= mTotalWidth) {
            mXOneOffset = 0;
        }
        if (mXTwoOffset >= mTotalWidth) {
            mXTwoOffset = 0;
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, 14);

    }

    private void resetPositionY() {
        int yOneInterval = mYPositions.length - mXOneOffset;
        System.arraycopy(mYPositions,  mXOneOffset, mResetOneYPositions, 0, yOneInterval);
        System.arraycopy(mYPositions, 0, mResetOneYPositions, yOneInterval, mXOneOffset);

        int yTwoInterval = mYPositions.length - mXTwoOffset;
        System.arraycopy(mYPositions,  mXTwoOffset, mResetTwoYPositions, 0, yTwoInterval);
        System.arraycopy(mYPositions, 0, mResetTwoYPositions, yTwoInterval, mXTwoOffset);
    }
}
