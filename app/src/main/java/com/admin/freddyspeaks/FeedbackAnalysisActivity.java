package com.admin.freddyspeaks;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import layout.RatingSummaryFragment;
import layout.SalesReportFragment;

public class FeedbackAnalysisActivity extends BaseActivity{

    private ImageView imageViewPieChart;
    private ImageView imageViewGraph;

    private TextView textViewPieChart;
    private TextView textViewPieGraph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_analysis);
        setTitle("");
        TabLayout tabLayout = (TabLayout) findViewById(R.id.feedbackTabs);
        tabLayout.addTab(tabLayout.newTab().setText("Ratings").setIcon(R.drawable.tab_logo_pie_chart));
        tabLayout.addTab(tabLayout.newTab().setText("Sales").setIcon(R.drawable.tab_logo_graph));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Set new view.
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);

            RelativeLayout relativeLayout;
            if (i == 0){
                relativeLayout = (RelativeLayout)
                        LayoutInflater.from(this).inflate(R.layout.tab_layout_without_divider, tabLayout, false);
            } else {
                relativeLayout = (RelativeLayout)
                        LayoutInflater.from(this).inflate(R.layout.tab_layout_with_divider, tabLayout, false);
            }

            // Set title.
            TextView tabTextView = (TextView) relativeLayout.findViewById(R.id.tab_title);
            tabTextView.setText(tab.getText());

            // Set logo.
            ImageView imageView = (ImageView) relativeLayout.findViewById(R.id.imageViewLogo);
            imageView.setImageDrawable(tab.getIcon());

            // Get images for changing.
            if (i == 0) {
                imageViewPieChart = imageView;
                textViewPieChart = tabTextView;
            } else {
                imageViewGraph = imageView;
                textViewPieGraph = tabTextView;

                imageViewGraph.setImageAlpha(80);
                textViewPieGraph.setAlpha(0.4f);
            }

            tab.setCustomView(relativeLayout);
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        FeedbackAnalysisAdapter feedbackAnalysisAdapter = new FeedbackAnalysisAdapter(getSupportFragmentManager());
        viewPager.setAdapter(feedbackAnalysisAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                // Set image alpha
                if (tab.getPosition() == 0){
                    imageViewGraph.setImageAlpha(80);
                    textViewPieGraph.setAlpha(0.4f);

                    imageViewPieChart.setImageAlpha(0xFF);
                    textViewPieChart.setAlpha(1.0f);
                } else {
                    imageViewGraph.setImageAlpha(0xFF);
                    textViewPieGraph.setAlpha(1.0f);

                    imageViewPieChart.setImageAlpha(80);
                    textViewPieChart.setAlpha(0.4f);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.getTabAt(0).getCustomView().setSelected(true);
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