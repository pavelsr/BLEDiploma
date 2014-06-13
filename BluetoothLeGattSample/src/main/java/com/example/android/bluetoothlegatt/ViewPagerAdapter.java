package com.example.android.bluetoothlegatt;





import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by noxa on 11/06/14.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {


    private ArrayList<BasicGraphFragment> fragmentsContainer = new ArrayList<BasicGraphFragment>(3);
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        BasicGraphFragment fragment;
        fragment = new NaturalGraphFragment();
        fragment.setMode(BasicGraphFragment.NATURAL);
        fragmentsContainer.add(fragment);

        fragment = new DerivGraphFragment();
        fragment.setMode(BasicGraphFragment.DERIV);
        fragmentsContainer.add(fragment);
        fragment = null;
    }

//    public BasicGraphFragment getItem(int pos) {
//        return fragmentsContainer.get(pos);
//    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsContainer.get(position);
    }

    @Override
    public int getCount() {
        return fragmentsContainer.size();
    }
}
