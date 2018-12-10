package org.publicntp.timeserver.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.publicntp.timeserver.R;
import org.publicntp.timeserver.helper.permissions.Permission;
import org.publicntp.timeserver.helper.permissions.PermissionsHelper;
import org.publicntp.timeserver.listener.LocationHelper;
import org.publicntp.timeserver.ui.about.AboutFragment;
import org.publicntp.timeserver.ui.satellite.SatelliteFragment;
import org.publicntp.timeserver.ui.server.ServerFragment;
import org.publicntp.timeserver.ui.time.TimeFragment;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, TabLayout.OnTabSelectedListener {
    @BindView(R.id.bottom_navigation) TabLayout tabLayout;
    @BindView(R.id.fragment_container) ViewPager viewPager;

    @BindColor(R.color.blue) int blue;
    @BindColor(R.color.greydark) int greydark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SectionPagerAdapter sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(sectionPagerAdapter.getTabView(i));
            onTabUnselected(tab);
        }
        onTabSelected(tabLayout.getTabAt(0));
        tabLayout.addOnTabSelectedListener(this);


        if (PermissionsHelper.permissionIsGranted(this, Permission.FINE_LOCATION)) {
            LocationHelper.registerNmeaListenerAndStartGettingFixes(this);
        } else {
            PermissionsHelper.requestPermission(this, Permission.FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Permission.FINE_LOCATION.getKey()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationHelper.registerNmeaListenerAndStartGettingFixes(this);
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        TextView tabLabelView = tab.getCustomView().findViewById(R.id.tab_label);
        tabLabelView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        TextView tabLabelView = tab.getCustomView().findViewById(R.id.tab_label);
        tabLabelView.setVisibility(View.GONE);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {
        private String[] mTabLabels;
        private int[] mTabIcons = {
                R.drawable.icon_nav_time_light_24px,
                R.drawable.icon_nav_satl_light_24px,
                R.drawable.icon_nav_serv_light_24px,
                R.drawable.icon_nav_info_light_24px
        };

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
            mTabLabels = getResources().getStringArray(R.array.tab_names);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TimeFragment.newInstance();
                case 1:
                    return SatelliteFragment.newInstance();
                case 2:
                    return ServerFragment.newInstance();
                case 3:
                    return AboutFragment.newInstance();
                default:
                    throw new RuntimeException("Bad position in viewpager");
            }
        }

        @Override
        public int getCount() {
            return getResources().getStringArray(R.array.tab_names).length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] names = getResources().getStringArray(R.array.tab_names);
            if (position < names.length) {
                return names[position];
            }
            return null;
        }

        View getTabView(int position) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.partial_custom_tab, null);
            ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
            icon.setImageResource(mTabIcons[position]);
            TextView label = (TextView) view.findViewById(R.id.tab_label);
            label.setText(mTabLabels[position]);
            return view;
        }
    }
}
