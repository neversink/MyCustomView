package com.example.xiachen.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by xiachen on 15/11/11.
 */
public class LuckyPanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;
    private boolean isRunning;
    private Thread t;
    private Canvas mCanvas;

    private String[] mStrs = new String[]{"单反相机", "IPAD", "恭喜发财", "IPHONE", "妹子一只", "恭喜发财"};

    private int[] mColors = new int[]{0xFFFFC300, 0xFFF17E01, 0xFFFFC300,
            0xFFF17E01, 0xFFFFC300, 0xFFF17E01};

    private int[] mImgs = new int[]{R.drawable.danfan, R.drawable.ipad,
            R.drawable.f040, R.drawable.iphone, R.drawable.meizi,
            R.drawable.f040};
    private Bitmap[] mImgsBitmap;
    private int mItemCount = mStrs.length;
    private RectF mRange = new RectF();
    private int mRadius;
    private Paint mArcPaint;
    private Paint mTextPaint;
    private double mSpeed = 0;
    private volatile float mStartAngle = 0;
    private boolean isShouldEnd;
    private int mCenter;
    private int mPadding = 0;
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
    private float mTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

    public LuckyPanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mRadius = width - getPaddingLeft() - getPaddingRight();
        mCenter = width / 2;
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        mTextPaint = new Paint();
        mTextPaint.setColor(0xFFffffff);
        mTextPaint.setTextSize(mTextSize);
        mRange = new RectF(getPaddingLeft(), getPaddingLeft(), mRadius
                + getPaddingLeft(), mRadius + getPaddingLeft());
        mImgsBitmap = new Bitmap[mItemCount];
        for (int i = 0; i < mItemCount; i++) {
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),
                    mImgs[i]);
        }

        // 开启线程
        isRunning = true;
        t = new Thread(this);
        t.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 50) {
                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                drawBg();
                float tmpAngle = mStartAngle;
                float sweepAngle = 360 / mItemCount;
                for (int i = 0; i < mItemCount; i++) {
                    mArcPaint.setColor(mColors[i]);
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
                    drawText(tmpAngle, sweepAngle, mStrs[i]);
                    drawIcon(tmpAngle, i);
                    tmpAngle += sweepAngle;
                }
                mStartAngle += mSpeed;
                if (isShouldEnd) {
                    mSpeed -= 1;
                }
                if (mSpeed < 0) {
                    mSpeed = 0;
                    isShouldEnd = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void drawIcon(float tmpAngle, int i) {
        int imgWidth = mRadius / 8;
        float angle = (float) ((360 / mItemCount / 2 + tmpAngle) * (Math.PI / 180));
        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));

        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth
                / 2, y + imgWidth / 2);

        mCanvas.drawBitmap(mImgsBitmap[i], null, rect, null);
    }

    private void drawText(float tmpAngle, float sweepAngle, String str) {
        Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);
        float textWidth = mTextPaint.measureText(str);
        float hOffset = (float) (Math.PI * mRadius / mItemCount / 2 - textWidth / 2);
        Log.w("never", "hOffset1 = " + (float) (mRadius / 2 * Math.sin(Math.PI*sweepAngle/360.0)));
        Log.w("never", "hOffset2 = " + (float) (Math.PI * mRadius / mItemCount / 2));

        float vOffset = mRadius / 2 / 6;
        mCanvas.drawTextOnPath(str, path, hOffset, vOffset, mTextPaint);
    }

    private void drawBg() {
        mCanvas.drawColor(0xFFFFFFFF);
        mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding / 2,
                mPadding / 2, getMeasuredWidth() - mPadding / 2,
                getMeasuredWidth() - mPadding / 2), null);
    }

    public void luckyStart(int luckIndex) {
        float angle = 360 / mItemCount;
        float from = 270 - (luckIndex + 1) * angle;
        float to = from + angle;
        float targetFrom = 360 * 4 + from;
        float v1 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetFrom) - 1) / 2;
        float targetTo = 4 * 360 + to;
        float v2 = (float) (Math.sqrt(1 * 1 + 8 * 1 * targetTo) - 1) / 2;
        mSpeed = (float) (v1 + Math.random() * (v2 - v1));
        isShouldEnd = false;
    }

    public void luckyEnd() {
        mStartAngle = 0;
        isShouldEnd = true;
    }


    public boolean isShouldEnd() {
        return isShouldEnd;
    }

    public boolean isStart() {
        return mSpeed != 0;
    }

}
