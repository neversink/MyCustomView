package com.example.xiachen.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by xiachen on 15/11/9.
 */
public class CircleMenuLayout extends ViewGroup {

    private int mRadius;
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
    private float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
    private static final float RADIO_PADDING_LAYOUT = 1 / 12f;
    private static final int FLINGABLE_VALUE = 300;
    private static final int NOCLICK_VALUE = 3;
    private int mFlingableValue;
    private float mPadding;
    private int[] mItemImgs;
    private String[] mItemTexts;
    private int mMenuItemCount;
    private float mStartAngle = 0;

    public void setMenuItemIconsAndTexts(int[] resIds, String[] texts) {
        mItemImgs = resIds;
        mItemTexts = texts;

        // 参数检查
        if (resIds == null && texts == null) {
            throw new IllegalArgumentException("菜单项文本和图片至少设置其一");
        }

        // 初始化mMenuCount
        mMenuItemCount = resIds == null ? texts.length : resIds.length;

        if (resIds != null && texts != null) {
            mMenuItemCount = Math.min(resIds.length, texts.length);
        }

        addMenuItems();

    }

    private void addMenuItems() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        /**
         * 根据用户设置的参数，初始化view
         */
        for (int i = 0; i < mMenuItemCount; i++) {
            final int j = i;
            View view = mInflater.inflate(R.layout.circle_menu_item, this, false);
            ImageView iv = (ImageView) view.findViewById(R.id.id_circle_menu_item_image);
            TextView tv = (TextView) view.findViewById(R.id.id_circle_menu_item_text);

            if (iv != null) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(mItemImgs[i]);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mOnMenuItemClickListener != null) {
                            mOnMenuItemClickListener.itemClick(v, j);
                        }
                    }
                });
            }
            if (tv != null) {
                tv.setVisibility(View.VISIBLE);
                tv.setText(mItemTexts[i]);
            }

            // 添加view到容器中
            addView(view);
        }
    }

    public interface OnMenuItemClickListener {
        void itemClick(View view, int pos);

        void itemCenterClick(View view);
    }

    private OnMenuItemClickListener mOnMenuItemClickListener;

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPadding(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            resWidth = getSuggestedMinimumWidth();
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;
            resHeight = getSuggestedMinimumHeight();
            resHeight = resHeight == 0 ? getDefaultHeight() : resHeight;
        } else {
            resWidth = resHeight = Math.min(width, height);
        }
        setMeasuredDimension(resWidth, resHeight);
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());
        final int count = getChildCount();
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        int centerSize = (int) (mRadius * RADIO_DEFAULT_CENTERITEM_DIMENSION);
        int childMode = MeasureSpec.EXACTLY;
        int makeMeasureSpec;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (child.getId() == R.id.id_circle_menu_item_center) {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(centerSize, childMode);
            } else {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            }
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
        mPadding = RADIO_PADDING_LAYOUT * mRadius;
    }

    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    private int getDefaultHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return Math.min(outMetrics.widthPixels, outMetrics.heightPixels);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutRadius = mRadius;
        final int childCount = getChildCount();
        int left, top;
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        float angleDelay = 360 / getChildCount() - 1;
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getId() == R.id.id_circle_menu_item_center)
                continue;

            if (child.getVisibility() == GONE) {
                continue;
            }
            float tmp = mRadius / 2 - cWidth / 2 - mPadding;
            left = (int) (layoutRadius / 2 + tmp * Math.cos(Math.toRadians(mStartAngle)) - cWidth / 2);
            top = (int) (layoutRadius / 2 + tmp * Math.sin(Math.toRadians(mStartAngle)) - cWidth / 2);
            child.layout(left, top, left + cWidth, top + cWidth);
            // 叠加尺寸
            mStartAngle += angleDelay;
        }
        View cView = findViewById(R.id.id_circle_menu_item_center);
        if (cView != null) {
            cView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.itemCenterClick(v);
                    }
                }
            });
            int cl = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
            int cr = cl + cView.getMeasuredWidth();
            cView.layout(cl, cl, cr, cr);
        }
    }
}