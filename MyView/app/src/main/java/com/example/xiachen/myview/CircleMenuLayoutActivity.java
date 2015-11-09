package com.example.xiachen.myview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by xiachen on 15/11/9.
 */
public class CircleMenuLayoutActivity extends AppCompatActivity {
    private CircleMenuLayout mCircleMenuLayout;

    private String[] mItemTexts = new String[]{"安全中心 ", "特色服务", "投资理财",
            "转账汇款", "我的账户", "信用卡"};
    private int[] mItemImgs = new int[]{R.drawable.home_mbank_1_normal,
            R.drawable.home_mbank_2_normal, R.drawable.home_mbank_3_normal,
            R.drawable.home_mbank_4_normal, R.drawable.home_mbank_5_normal,
            R.drawable.home_mbank_6_normal};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitycircemenulayout);

        mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.id_menulayout);
        mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);


        mCircleMenuLayout.setOnMenuItemClickListener(new CircleMenuLayout.OnMenuItemClickListener() {

            @Override
            public void itemClick(View view, int pos) {
                Toast.makeText(CircleMenuLayoutActivity.this, mItemTexts[pos],
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void itemCenterClick(View view) {
                Toast.makeText(CircleMenuLayoutActivity.this,
                        "you can do something just like ccb  ",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }
}
