package com.uclab.everytree.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.uclab.everytree.ui.UserTabs.Tab1;
import com.uclab.everytree.ui.UserTabs.Tab2;
import com.uclab.everytree.ui.UserTabs.Tab3;

public class UserTabAdapter extends FragmentPagerAdapter {
    private final static int NUM_TABS = 3;

    public UserTabAdapter(FragmentManager fm){
        super(fm);
    }

    @Override    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Tab1();
            case 1:
                return new Tab2();
            case 2:
                return new Tab3();
        }
        return null;
    }
    @Override
    public int getCount() {
        return NUM_TABS;
    }
}
