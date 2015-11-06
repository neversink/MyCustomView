package com.example.xiachen.myview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;

/**
 * Created by xiachen on 15/11/5.
 */
public class StickyNavLayout extends LinearLayout{
    private View mTop;
    private View mNav;
    private ViewPager mViewPager;
    private ScrollView mInnerScrollView;

    private int mTopViewHeight;
    private boolean isTopHidden = false;

    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMaximumVelocity, mMinimumVelocity;

    private float mLastY;
    private boolean mDragging;

    public StickyNavLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScroller = new OverScroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTop = findViewById(R.id.id_stickynavlayout_topview);
        mNav = findViewById(R.id.id_indicator);
        View view = findViewById(R.id.id_stickynavlayout_viewpager);
        if (!(view instanceof ViewPager)) {
            throw new RuntimeException("id_stickynavlayout_viewpager show used by ViewPager !");
        }
        mViewPager = ((ViewPager) view);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup.LayoutParams lp = mViewPager.getLayoutParams();
        lp.height = getMeasuredHeight() - mNav.getMeasuredHeight();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTop.getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(event);
                mLastY = y;
                return true;
        }



        return true;
    }
}
