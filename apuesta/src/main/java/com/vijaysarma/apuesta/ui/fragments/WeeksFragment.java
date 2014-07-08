package com.vijaysarma.apuesta.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.vijaysarma.apuesta.R;
import com.vijaysarma.apuesta.misc.WeekOptions;
import com.vijaysarma.apuesta.views.AnimatedFrameLayout;
import com.vijaysarma.apuesta.views.DepthPageTransformer;
import com.vijaysarma.apuesta.views.ZoomOutPageTransformer;

import butterknife.InjectView;

public class WeeksFragment extends BaseFragment {

    @InjectView(R.id.pager)
    ViewPager viewPager;

    @InjectView(R.id.view)
    AnimatedFrameLayout container;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeekOptions options = new WeekOptions.WeekOptionsBuilder().build();
        viewPager.setAdapter(new WeeksPagerAdapter(getFragmentManager(), options));
        viewPager.setCurrentItem(options.getCurrentWeek() - 1);
//        viewPager.setPageTransformer(true, new DepthPageTransformer());
//        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        container.setDisplayedChildId(R.id.pager);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_weeks;
    }

    private class WeeksPagerAdapter extends FragmentStatePagerAdapter {
        private final WeekOptions options;

        public WeeksPagerAdapter(FragmentManager fm, WeekOptions options) {
            super(fm);
            this.options = options;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Week " + (position + 1);
        }

        @Override
        public Fragment getItem(int position) {
            int[] weeks = options.getWeeks();
            int week = weeks[position];
            int year = options.getYear();
            String type = options.getType();

            return WeekFragment.create(year, week, type);
//            return WeekView.create(year, week, type);
        }

        @Override
        public int getCount() {
            return options.getWeeks().length;
        }
    }
}

