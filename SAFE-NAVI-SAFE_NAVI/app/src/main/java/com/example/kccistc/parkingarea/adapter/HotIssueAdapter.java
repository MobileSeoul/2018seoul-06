package com.example.kccistc.parkingarea.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kccistc.parkingarea.fragment.Fragment_First;
import com.example.kccistc.parkingarea.fragment.Fragment_Second;
import com.example.kccistc.parkingarea.fragment.Fragment_Third;

public class HotIssueAdapter extends FragmentPagerAdapter {
    Fragment fragment = null;

    public HotIssueAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int i) {
        if(i==0)
            return "네이버";
        else if(i==1)
            return "다음";
        else
            return "영화진흥공사";

    }

    @Override
    public Fragment getItem(int i) {

        if(i==0)
            fragment = new Fragment_First();
        else if(i==1)
            fragment = new Fragment_Second();
        else if(i==2)
            fragment = new Fragment_Third();

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
