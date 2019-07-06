package com.uclab.everytree.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.uclab.everytree.R;
import com.uclab.everytree.adapters.UserTabAdapter;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = UserActivity.class.getSimpleName();
    private int[] tabIcons = {
            R.drawable.ic_info_black_24dp,
            R.drawable.ic_photo_camera_black_24dp,
            R.drawable.ic_score_black_24dp
    };
    private int[] tabHeaders = {
            R.string.infoHeader,
            R.string.photoHeader,
            R.string.scoreHeader
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTabs();
    }

    private void setTabs() {
        ViewPager pager = findViewById(R.id.pager);
        UserTabAdapter tabAdapter = new UserTabAdapter(getSupportFragmentManager());
        pager.setAdapter(tabAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        for (int i = 0; i < tabAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);

            if (tab != null) {
                tab.setText(tabHeaders[i]);
                tab.setIcon(tabIcons[i]);
            }
        }
    }
}