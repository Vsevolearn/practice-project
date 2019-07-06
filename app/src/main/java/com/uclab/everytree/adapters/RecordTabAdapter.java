package com.uclab.everytree.adapters;

import com.uclab.everytree.ui.TreeRecordTabs.Tab1;
import com.uclab.everytree.ui.TreeRecordTabs.Tab2;
import com.uclab.everytree.ui.TreeRecordTabs.Tab3;
import com.uclab.everytree.ui.TreeRecordTabs.Tab4;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class RecordTabAdapter extends FragmentPagerAdapter {
    private final static int NUM_TABS = 4;

    public RecordTabAdapter(FragmentManager fm){
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
            case 3:
                return new Tab4();
        }
        return null;
    }
    @Override
    public int getCount() {
        return NUM_TABS;
    }
}
