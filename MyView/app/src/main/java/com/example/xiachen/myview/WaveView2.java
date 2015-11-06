package com.example.xiachen.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by xiachen on 15/11/4.
 */
public class WaveView2 extends View{

    private static final int WAVE_TRANS_SPEED = 4;
    private Paint mBitmapPaint, mPicPaint;
    private int mTotalWidth, mTotalHeight;
    private int mCenterX, mCenterY;
    private int mSpeed;
    private Bitmap mSrcBitmap, mMaskBitmap;
    private Rect mSrcRect, mDestRect;
    private Rect mMaskSrcRect, mMaskDestRect;
    private PorterDuffXfermode mPorterDuffXfermode;
    private PaintFlagsDrawFilter mDrawFilter;
    private int mCurrentPosition;
    private boolean flag = true;

    public WaveView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initBitmap();
        mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mSpeed = dipToPx(context, WAVE_TRANS_SPEED);
        mDrawFilter = new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, Paint.DITHER_FLAG);
        new Thread() {
            @Override
            public void run() {
                while (flag) {
                    mCurrentPosition += mSpeed;
                    if (mCurrentPosition > mSrcBitmap.getWidth()) {
                        mCurrentPosition = 0;
                    }
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    postInvalidate();
                }
            }
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);
//        canvas.drawColor(Color.TRANSPARENT);
        int sc = canvas.saveLayer(0, 0, mTotalWidth, mTotalHeight, null, canvas.ALL_SAVE_FLAG);
        mSrcRect.set(mCurrentPosition, 0, mCurrentPosition + mCenterX, mTotalHeight);
        canvas.drawBitmap(mSrcBitmap, mSrcRect, mDestRect, mBitmapPaint);

        mBitmapPaint.setXfermode(mPorterDuffXfermode);
        canvas.drawBitmap(mMaskBitmap, mMaskSrcRect, mMaskDestRect, mBitmapPaint);
        mBitmapPaint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    private int dipToPx(Context context, int waveTransSpeed) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return (int) (waveTransSpeed * metrics.density + 0.5f);
    }

    private void initPaint() {
        mBitmapPaint = new Paint();
        mBitmapPaint.setDither(true);
        mBitmapPaint.setFilterBitmap(true);

        mPicPaint = new Paint();
        mPicPaint.setAntiAlias(true);
//        mPicPaint.setColor(Color.RED);

    }

    private void initBitmap() {
        mSrcBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.wave_2000)).getBitmap();
        mMaskBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.circle_500)).getBitmap();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTotalWidth = w;
        mTotalHeight = h;
        mCenterX = mTotalWidth / 2;
        mCenterY = mTotalHeight / 2;
        mSrcRect = new Rect();
        mDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);
        int maskWidth = mMaskBitmap.getWidth();
        int maskHeight = mMaskBitmap.getHeight();
        mMaskSrcRect = new Rect(0, 0, maskWidth, maskHeight);
        mMaskDestRect = new Rect(0, 0, mTotalWidth, mTotalHeight);

    }
}
