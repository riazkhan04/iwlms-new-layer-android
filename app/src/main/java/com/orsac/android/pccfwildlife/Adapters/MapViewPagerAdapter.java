package com.orsac.android.pccfwildlife.Adapters;

import com.orsac.android.pccfwildlife.Fragments.ElephantMapAnalyticsFragment;
import com.orsac.android.pccfwildlife.Fragments.MapFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MapViewPagerAdapter extends FragmentPagerAdapter {


    public MapViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        try {
            if (position == 0)
            {
                fragment = new MapFragment();
            }
            else if (position == 1)
            {
                fragment = new ElephantMapAnalyticsFragment();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        try {

            if (position == 0)
            {
                title = "Elephant Sighting";
            }
            else if (position == 1)
            {
                title = "Elephant Analytics";
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return title;
    }

}
