package com.admin.freddyspeaks;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import layout.RatingSummaryFragment;
import layout.SalesReportFragment;

public class FeedbackAnalysisActivity extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_analysis);
        setTitle("");
        TabLayout tabLayout = (TabLayout) findViewById(R.id.feedbackTabs);
        tabLayout.addTab(tabLayout.newTab().setText("Ratings"));
        tabLayout.addTab(tabLayout.newTab().setText("Sales Report"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        FeedbackAnalysisAdapter feedbackAnalysisAdapter = new FeedbackAnalysisAdapter(getSupportFragmentManager());
        viewPager.setAdapter(feedbackAnalysisAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void closeActivity(View v) {
        this.finish();
    }


    public class FeedbackAnalysisAdapter extends FragmentPagerAdapter {

        final int numOfTabs = 2;

        public FeedbackAnalysisAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    RatingSummaryFragment ratingSummaryFragment = new RatingSummaryFragment();
                    return ratingSummaryFragment;
                case 1:
                    SalesReportFragment salesReportFragment = new SalesReportFragment();
                    return salesReportFragment;
            }
            return null;
        }

    }

}