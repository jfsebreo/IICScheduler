package com.ruthere.application.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruthere.application.activities.R;

public class QueueFragment extends Fragment {

    public static ViewPager viewPager;
    public static TabLayout tabLayout;
    public static int int_items = 3 ;

    public QueueFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return rootView;
    }

    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new ApprovedFragment();
                case 1 : return new DiscardedFragment();
                case 2 : return new PendingFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Approved";
                case 1 :
                    return "Rejected";
                case 2 :
                    return "Pending";
            }
            return null;
        }
    }
}
