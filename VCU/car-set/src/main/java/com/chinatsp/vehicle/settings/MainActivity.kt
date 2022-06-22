package com.chinatsp.vehicle.settings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.vehicle.settings.databinding.MainActivityTablayoutBinding
import com.chinatsp.vehicle.settings.fragment.AccessFragment
import com.chinatsp.vehicle.settings.fragment.cabin.CabinManagerFragment
import com.chinatsp.vehicle.settings.fragment.SimpleTabFragment
import com.chinatsp.vehicle.settings.fragment.doors.DoorsManageFragment
import com.chinatsp.vehicle.settings.fragment.drive.DriveManageFragment
import com.chinatsp.vehicle.settings.fragment.lighting.LightingManageFragment
import com.chinatsp.vehicle.settings.fragment.sound.SoundManageFragment
import com.chinatsp.vehicle.settings.vm.MainViewModel
import com.common.library.frame.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.common.xui.utils.ViewUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, MainActivityTablayoutBinding>(),
    OnTabSelectedListener {

    private val mAdapter: FragmentStateViewPager2Adapter by lazy {
        FragmentStateViewPager2Adapter(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.main_activity_tablayout
    }

    override fun isBinding(): Boolean = true

    override fun initData(savedInstanceState: Bundle?) {
        initTabLayout()
    }

    private fun initTabLayout() {
        with(this) {
            binding.tabLayout.tabMode = TabLayout.MODE_AUTO
            binding.tabLayout.addOnTabSelectedListener(this)
            binding.viewPager.adapter = mAdapter
            binding.viewPager.isUserInputEnabled = false
            // 设置缓存的数量
            binding.viewPager.offscreenPageLimit = 1
            val childAt: RecyclerView = binding.viewPager.getChildAt(0) as RecyclerView
            childAt.setItemViewCacheSize(0)
            childAt.layoutManager?.isItemPrefetchEnabled = false
            TabLayoutMediator(
                binding.tabLayout,
                binding.viewPager,
                true,
                false
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = mAdapter.getPageTitle(position)
            }.attach()
            refreshAdapter(true)
        }
    }

    private fun refreshStatus(show: Boolean) {
//        val rotation: ObjectAnimator
        val tabAlpha: ObjectAnimator
        val viewAlpha: ObjectAnimator
//        val textAlpha: ObjectAnimator
        if (show) {
//            rotation = ObjectAnimator.ofFloat(ivSwitch, "rotation", 0f, -45f)
            tabAlpha = ObjectAnimator.ofFloat(binding.tabLayout, "alpha", 0f, 1f)
            viewAlpha = ObjectAnimator.ofFloat(binding.viewPager, "alpha", 0f, 1f)
//            textAlpha = ObjectAnimator.ofFloat(tvTitle, "alpha", 1f, 0f)
        } else {
//            rotation = ObjectAnimator.ofFloat(ivSwitch, "rotation", -45f, 0f)
            tabAlpha = ObjectAnimator.ofFloat(binding.tabLayout, "alpha", 1f, 0f)
            viewAlpha = ObjectAnimator.ofFloat(binding.viewPager, "alpha", 1f, 0f)
//            textAlpha = ObjectAnimator.ofFloat(tvTitle, "alpha", 0f, 1f)
        }
        val animatorSet = AnimatorSet()
//        animatorSet.play(rotation).with(tabAlpha).with(textAlpha)
        animatorSet.play(tabAlpha).with(viewAlpha)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                refreshAdapter(show)
            }

            override fun onAnimationEnd(animation: Animator) {
                switchContainer(show)
            }
        })
        animatorSet.setDuration(500).start()
    }

    private fun switchContainer(show: Boolean) {
        ViewUtils.setVisibility(binding.viewPager, show)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdapter(isShow: Boolean) {
        if (isShow) {
            // 动态加载选项卡内容
            for (page in viewModel.liveDataTabPage.value!!) {
                val title = page.description
                val position = page.position
                if (position == 0) {
                    val fragment = AccessFragment::class.java
//                    fragment.userVisibleHint = true
                    mAdapter.addFragment(fragment, title)
                } else if (position == 1) {
                    val fragment = DoorsManageFragment::class.java
                    mAdapter.addFragment(fragment, title)
                } else if (position == 2) {
                    val fragment = LightingManageFragment::class.java
                    mAdapter.addFragment(fragment, title)
                } else if (position == 3) {
                    val fragment = SoundManageFragment::class.java
                    mAdapter.addFragment(fragment, title)
                } else if (position == 4) {
                    val fragment = CabinManagerFragment::class.java
                    mAdapter.addFragment(fragment, title)
                } else if (position == 5) {
                    val fragment = DriveManageFragment::class.java
                    mAdapter.addFragment(fragment, title)
                } else {
                    mAdapter.addFragment(SimpleTabFragment::class.java, title)
                }
            }
            mAdapter.notifyDataSetChanged()
            binding.viewPager.setCurrentItem(GlobalManager.instance.tabSerial.get(), false)
        } else {
            mAdapter.clear()
        }
    }


    fun onClick(view: View) {
        when (view.id) {
            R.id.back -> this.onBackPressed()
            else -> {
            }
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
            if (tab.text?.equals("常用") == true) {
                binding.constraint.setBackgroundResource(R.drawable.bg_changyong_1920)
            } else {
                binding.constraint.setBackgroundResource(R.drawable.bg)
            }
        }
        GlobalManager.instance.tabSerial.set(binding.tabLayout.selectedTabPosition)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}