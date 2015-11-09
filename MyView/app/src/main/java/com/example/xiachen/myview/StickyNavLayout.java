package com.example.xiachen.myview;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
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
public class StickyNavLayout extends LinearLayout {
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
    private boolean isInControl = false;

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
        mNav = findViewById(R.id.id_stickynavlayout_indicator);
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
//        lp.height = getMeasuredHeight() - mNav.getMeasuredHeight();
        lp.height = getMeasuredHeight();
        Log.w("++++", "getMeasuredHeight() = " + getMeasuredHeight() + "\n" +
                        "mNav.getMeasuredHeight() = " + mNav.getMeasuredHeight() + "\n"

        );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTopViewHeight = mTop.getMeasuredHeight();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.w("------", "onTouchEvent" + event.getAction());
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastY = y;
                //强制消耗事件
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                if (!mDragging && Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
                }
                if (mDragging) {
                    scrollBy(0, -((int) dy));

                    if (getScrollY() == mTopViewHeight && dy < 0) {
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        isInControl = false;
                    }
                }
                mLastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                recycleVelocityTracker();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float yVelocity = mVelocityTracker.getYVelocity();
                if (Math.abs(yVelocity) > mMinimumVelocity) {
                    fling(-yVelocity);
                }
                mVelocityTracker.clear();
                break;
        }


        return super.onTouchEvent(event);
    }

    private void fling(float yVelocity) {
        mScroller.fling(0, getScrollY(), 0, ((int) yVelocity), 0, 0, 0, mTopViewHeight);
        invalidate();
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mTopViewHeight) {
            y = mTopViewHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
        isTopHidden = getScrollY() == mTopViewHeight;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        Log.w("------", "onInterceptTouchEvent" + ev.getAction());
        float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                getCurrentScrollView();
//                Log.w("-", "here?" + dy);
                if (Math.abs(dy) > mTouchSlop) {
                    mDragging = true;
//                    Log.w("-", "isTopHidden2: rollView.getScrollY()2: " + mInnerScrollView.getScrollY() + "\n" +
//                                    "dy2: " + dy + "\n"
//                    );
                    Log.w("------", "dy vs slop: " + dy + "  " + mTouchSlop);
                    if (!isTopHidden || (isTopHidden && dy > 0 && mInnerScrollView.getScrollY() == 0)) {
                        Log.w("------", "reach here?");
                        mLastY = y;
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.w("------", "dispatchTouchEvent" + ev.getAction());
        int action = ev.getAction();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                getCurrentScrollView();
                if (mInnerScrollView instanceof ScrollView) {
                    if (isTopHidden && mInnerScrollView.getScrollY() == 0 && dy > 0 && !isInControl) {
                        isInControl = true;
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        MotionEvent ev2 = MotionEvent.obtain(ev);
                        dispatchTouchEvent(ev);
                        ev2.setAction(MotionEvent.ACTION_DOWN);
                        Log.w("------", "new dispatchtouchevent send");
                        return dispatchTouchEvent(ev2);
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
//        return true;
    }

    private void getCurrentScrollView() {
        int currentItem = mViewPager.getCurrentItem();
        PagerAdapter a = mViewPager.getAdapter();
        if (a instanceof FragmentPagerAdapter) {
            FragmentPagerAdapter fadapter = (FragmentPagerAdapter) a;
            Fragment item = (Fragment) fadapter.instantiateItem(mViewPager, currentItem);
            mInnerScrollView = (ScrollView) item.getView().findViewById(R.id.id_stickynavlayout_innerscrollview);
        } else if (a instanceof FragmentStatePagerAdapter) {
            FragmentStatePagerAdapter fsAdapter = (FragmentStatePagerAdapter) a;
            Fragment item = (Fragment) fsAdapter.instantiateItem(mViewPager, currentItem);
            mInnerScrollView = (ScrollView) item.getView().findViewById(R.id.id_stickynavlayout_innerscrollview);
        }

    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker = null;
        }
    }
}
