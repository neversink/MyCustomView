package com.example.xiachen.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xiachen on 15/11/5.
 */
public class SimpleViewPagerIndicator extends LinearLayout {

    private Paint mPaint;
    private Path mPath;
    private int mTriangleWidth;
    private int mTriangleHeight;
    private static final float RADIO_TRIANGLE = 1.0f / 6;
    private final int DIMENSION_TRIANGEL_WIDTH = (int) (getScreenWidth() / 3 * RADIO_TRIANGLE);


    private int mInitTranslationX;
    private float mTranslationX;
    private static final int COUNT_DEFAULT_TAB = 4;
    private int mTabVisibleCount = COUNT_DEFAULT_TAB;
    private static final int COLOR_TEXT_NORMAL = 0xFF00FF00;
    private static final int COLOR_TEXT_HIGHLIGHTCOLOR = 0xFFFF0000;
    private List<String> mTabTitles;
    private Context mContext;

    public ViewPager mViewPager;

    public SimpleViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mTabVisibleCount = ta.getInt(R.styleable.ViewPagerIndicator_item_count, COUNT_DEFAULT_TAB);
        if (mTabVisibleCount <= 0) {
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        }
        ta.recycle();

        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ff0000ff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();
        if (cCount == 0) return;
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(lp);
        }
        setItemClickEvent();
    }

    private void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGLE);
        mTriangleWidth = Math.min(DIMENSION_TRIANGEL_WIDTH, mTriangleWidth);
        mTriangleHeight = (int) (mTriangleWidth / 2 / Math.sqrt(2));

        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();

        mInitTranslationX = getWidth() / mTabVisibleCount / 2 - mTriangleWidth / 2;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();
        canvas.translate(mInitTranslationX + mTranslationX, getHeight() + 1);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    public void setViewPager(ViewPager viewPager, int pos) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                scroll(position, positionOffset);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position,
                            positionOffset, positionOffsetPixels);
                }
                // 回调

            }

            @Override
            public void onPageSelected(int position) {
                resetTextViewColor();
                highLightTextView(position);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }

            }
        });
        mViewPager.setCurrentItem(pos);
        highLightTextView(pos);
    }

    public void scroll(int position, float offset) {
//        mTranslationX = getWidth() / mTabVisibleCount * (position + offset);
        int tabWidth = getScreenWidth() / mTabVisibleCount;
        mTranslationX = tabWidth * (position + offset);
        Log.w("====", "mTranslationX = " + mTranslationX + "\n" +
                        "getWidth() = " + getWidth() + "\n" +
                        "getScreenWidth() = " + getScreenWidth() + "\n" +
                        "position = " + position + "\n" +
                        "offset = " + offset + "\n" +
                        "tabWidth = " + tabWidth
        );
        if (offset > 0 && position >= (mTabVisibleCount - 2) && getChildCount() > mTabVisibleCount && position < getChildCount() - 2) {
            if (mTabVisibleCount != 1) {
                this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth + (int) (tabWidth * offset), 0);
            } else {
//                this.scrollTo(position * tabWidth + (int) (tabWidth * offset), 0);
            }
        }
        invalidate();
    }

    private void highLightTextView(int pos) {
        View view = getChildAt(pos);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHTCOLOR);
        }
    }

    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    public interface PageChangeListener {
        void onPageScrollStateChanged(int i);

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);


        void onPageSelected(int position);
    }

    private PageChangeListener onPageChangeListener;

    public void setPageChangeListener(PageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public void setVisibleTabCount(int count) {
        this.mTabVisibleCount = count;
    }

    public void setTabItemTitles(List<String> datas) {
        if (datas != null && datas.size() > 0) {
            this.removeAllViews();
            this.mTabTitles = datas;

            for (String title : mTabTitles) {
                addView(generateTextView(title));
            }
            if (getChildCount() < mTabVisibleCount) {
                Log.w("++", "mTabVisibleCount" + mTabVisibleCount + "\n" +
                                "getChildCount()" + getChildCount() + "\n");
                mTabVisibleCount = getChildCount();

            }
            setItemClickEvent();
        }

    }

    private TextView generateTextView(String title) {
        TextView tv = new TextView(mContext);
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setText(title);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setLayoutParams(lp);
        return tv;
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;

    }
}
