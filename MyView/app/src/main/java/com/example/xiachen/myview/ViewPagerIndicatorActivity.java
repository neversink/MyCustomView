package com.example.xiachen.myview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xiachen on 15/11/5.
 */
public class ViewPagerIndicatorActivity extends FragmentActivity {

    private List<Fragment> mTabContents = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private List<String> mDatas = Arrays.asList("短信", "收藏", "推荐", "短信", "收藏", "推荐", "短信", "收藏", "推荐");
    private SimpleViewPagerIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpagerindicatorlayout);

        initView();
        initDatas();
        mIndicator.setTabItemTitles(mDatas);
        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager, 0);

    }

    private void initDatas() {
        for (String data : mDatas) {
            TabFragment fragment = TabFragment.newInstance(data);
            mTabContents.add(fragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }
        };
    }

    private void initView() {
        {
            mViewPager = (ViewPager) findViewById(R.id.id_vp);
            mIndicator = (SimpleViewPagerIndicator) findViewById(R.id.id_indicator);
        }

    }

}
