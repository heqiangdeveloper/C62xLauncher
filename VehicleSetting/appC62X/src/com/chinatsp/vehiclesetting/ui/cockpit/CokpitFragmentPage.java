package com.chinatsp.vehiclesetting.ui.cockpit;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.chinatsp.vehiclesetting.MainActivity;
import com.chinatsp.vehiclesetting.R;
import com.chinatsp.vehiclesetting.adapter.FragmentPagerAdapter;
import com.chinatsp.vehiclesetting.base.BaseFragment;
import com.chinatsp.vehiclesetting.databinding.FragmentCockpitPageBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.QTabView;

public class CokpitFragmentPage extends BaseFragment<FragmentCockpitPageBinding> {
    /*设置Tab集合*/
    private int[] tabTitle = {R.string.cockpit_title_name_steering_wheel, R.string.cockpit_title_name_seat, R.string.cockpit_title_name_conditioner, R.string.cockpit_title_name_safety, R.string.cockpit_title_name_instrument};

    /**
     * 菜单图标
     */
    private final int[] tabImage = new int[]{R.drawable.tab_cokpit_steering_selector, R.drawable.tab_cokpit_seat_selector, R.drawable.tab_cokpit_ac_selector, R.drawable.tab_cokpit_safe_selector
            , R.drawable.tab_cokpit_xunh_selector};

    private List<Fragment> mFragmentList;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initTitle();
        initFragment();
        //initAdapter();
    }

   /* private void initTitle() {
        for (int i = 0; i < tabTitle.length; i++) {
            TabLayout.Tab tab = binding.tabLayout.newTab();
            View view = getLayoutInflater().inflate(R.layout.item_cokpit_menu, null);
            // 使用自定义视图，目的是为了便于修改，也可使用自带的视图
            tab.setCustomView(view);
            TextView tvTitle = view.findViewById(R.id.txt_tab);
            tvTitle.setText(tabTitle[i]);
            ImageView imgTab = view.findViewById(R.id.img_tab);
            imgTab.setImageResource(tabImage[i]);
            binding.tabLayout.addTab(tab);
        }
    }*/

    /*添加fragment*/
    private void initFragment() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new CockpitSteeringFragment());
        mFragmentList.add(new CockpitSeatFragment());
        mFragmentList.add(new CockpitAcFragment());
        mFragmentList.add(new CockpitSafeFragment());
        mFragmentList.add(new CockpitXunhFragment());
    }

    private void initData() {
        binding.tabLayout.setTabAdapter(new TabAdapter() {
            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Override
            public int getBadge(int position) {
                return position;
            }

            @Override
            public QTabView.TabIcon getIcon(int position) {
                return null;
            }

            @Override
            public QTabView.TabTitle getTitle(int position) {
                QTabView.TabTitle title = new QTabView.TabTitle.Builder(MainActivity.this)
                        .setContent(mTitlesList.get(position))//设置数据   也有设置字体颜色的方法
                        .build();
                return title;
            }

            @Override
            public int getBackground(int position) {
                return R.drawable.tab_selector;//选中的背景颜色
            }
        });
    }

    /*关联tab与fragment视图*/
   /* private void initAdapter() {
        binding.viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(), mFragmentList, tabTitle));
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        *//*设置viewpage默认页面，0是第一页，1是第二页*//*
        binding.viewPager.setCurrentItem(0);

        *//*设置Tab点击监听器*//*
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                *//*Tab点击选中时对应viewpage视图*//*
                binding.viewPager.setCurrentItem(tab.getPosition());

            }

            *//*标签没选中时对应视图*//*
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }*/
}
