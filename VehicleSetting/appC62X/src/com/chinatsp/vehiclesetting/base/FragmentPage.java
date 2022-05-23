package com.chinatsp.vehiclesetting.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.chinatsp.vehiclesetting.R;
import com.chinatsp.vehiclesetting.adapter.FragmentPagerAdapter;
import com.chinatsp.vehiclesetting.databinding.FragmentPageBinding;
import com.chinatsp.vehiclesetting.ui.beijing.BeijingFragment;
import com.chinatsp.vehiclesetting.ui.cockpit.CockpitFragment;
import com.chinatsp.vehiclesetting.ui.commonly.CommonlyFragment;
import com.chinatsp.vehiclesetting.ui.doors.DoorsFragment;
import com.chinatsp.vehiclesetting.ui.driving.DrivingFragment;
import com.chinatsp.vehiclesetting.ui.lighting.LightingFragment;
import com.chinatsp.vehiclesetting.ui.vehicle.VehicleFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FragmentPage extends BaseFragment<FragmentPageBinding> {
    /*设置Tab集合*/
    private int[] tabTitle = {R.string.main_title_name_commonly, R.string.main_title_name_car, R.string.main_title_name_light, R.string.main_title_name_sound, R.string.main_title_name_cockpit, R.string.main_title_name_driving, R.string.main_title_name_beijing};

    private List<Fragment> mFragmentList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initTitle();
        //initFragment();
        //initAdapter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTitle();
        initFragment();
        initAdapter();
    }

    private void initTitle() {
        for (int i = 0; i < tabTitle.length; i++) {
            TabLayout.Tab tab = binding.tabLayout.newTab();
            View view = getLayoutInflater().inflate(R.layout.item_main_menu, null);
            // 使用自定义视图，目的是为了便于修改，也可使用自带的视图
            tab.setCustomView(view);
            TextView tvTitle = view.findViewById(R.id.txt_tab);
            tvTitle.setText(tabTitle[i]);
            binding.tabLayout.addTab(tab);
        }
    }

    /*添加fragment*/
    private void initFragment() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new CommonlyFragment());
        mFragmentList.add(new DoorsFragment());
        mFragmentList.add(new LightingFragment());
        mFragmentList.add(new VehicleFragment());
        mFragmentList.add(new CockpitFragment());
        mFragmentList.add(new DrivingFragment());
        mFragmentList.add(new BeijingFragment());
    }

    /*关联tab与fragment视图*/
    private void initAdapter() {
        binding.viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), mFragmentList, tabTitle));
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        /*设置viewpage默认页面，0是第一页，1是第二页*/
        binding.viewPager.setCurrentItem(0);

        /*设置Tab点击监听器*/
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                /*Tab点击选中时对应viewpage视图*/
                //binding.viewPager.setCurrentItem(tab.getPosition());
                // 取消平滑切换
                binding.viewPager.setCurrentItem(tab.getPosition(), false);
            }

            /*标签没选中时对应视图*/
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
