package com.chinatsp.vehiclesetting.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.List;

public class FragmentPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {
    public FragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    private int[] tabTilte;
    private List<Fragment> mFragmentList;

    /*获取Tab标题*/
    public FragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, int[] tabTitle) {
        super(fm);
        this.mFragmentList = fragmentList;
        this.tabTilte = tabTitle;
    }

    /*获取viewpage位置*/
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    /*获取Tab集合长度，即获取有多少个Tab标题*/
    @Override
    public int getCount() {
        return tabTilte.length;
    }
}
