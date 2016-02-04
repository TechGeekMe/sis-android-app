package com.techgeekme.sis;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by anirudh on 15/01/16.
 */
public class HomePageAdapter extends FragmentPagerAdapter {

    public HomePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new SisFragment();
    }

    @Override
    public int getCount() {
        return 1;
    }
}
