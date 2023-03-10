package com.chinatsp.vehicle.settings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.chinatsp.settinglib.Constant
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.GlobalManager
import com.chinatsp.settinglib.navigation.RouterSerial
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.bean.TabPage
import com.chinatsp.vehicle.settings.databinding.MainActivityTablayoutBinding
import com.chinatsp.vehicle.settings.fragment.CommonlyFragment
import com.chinatsp.vehicle.settings.fragment.cabin.CabinManagerFragment
import com.chinatsp.vehicle.settings.fragment.doors.DoorsManageFragment
import com.chinatsp.vehicle.settings.fragment.adas.DriveManageFragment
import com.chinatsp.vehicle.settings.fragment.lighting.LightingManageFragment
import com.chinatsp.vehicle.settings.fragment.sound.SoundManageFragment
import com.chinatsp.vehicle.settings.vm.MainViewModel
import com.common.library.frame.base.BaseActivity
import com.common.xui.utils.ViewUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, MainActivityTablayoutBinding>(),
    OnTabSelectedListener, IRoute {

    val manager: GlobalManager
        get() = GlobalManager.instance

    private var firstCreate = true

    private val tabLocation: MutableLiveData<Int> by lazy {
        MutableLiveData(manager.getTabSerial())
    }

    private val level2Node: MutableLiveData<Node> by lazy { MutableLiveData(Node()) }

    private val popupLiveData: MutableLiveData<String> by lazy { MutableLiveData("") }

    private val mAdapter: FragmentStateViewPager2Adapter by lazy {
        FragmentStateViewPager2Adapter(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.main_activity_tablayout
    }

    override fun isBinding(): Boolean = true

    override fun initData(savedInstanceState: Bundle?) {
        initTabLayout()
        checkOutRoute(intent)
        tabLocation.observe(this) {
            manager.setTabSerial(it)
            if (it != binding.tabLayout.selectedTabPosition) {
                binding.viewPager.setCurrentItem(it, false)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkOutRoute(intent)
    }

    private fun checkOutRoute(intent: Intent?) {
        intent?.let {
            val value = it.getIntExtra(Constant.ROUTE_SERIAL, Constant.INVALID)
            if (Constant.INVALID != value) {
                val level1 = RouterSerial.getLevel(value, 1)
                val level2 = RouterSerial.getLevel(value, 2)
                val level3 = RouterSerial.getLevel(value, 3)
//                node = Node(level1)
                val node2 = level2Node.value
                node2?.uid = level2
                node2?.pid = level1
//                node2?.valid = true
                val value = it.getStringExtra(Constant.DIALOG_SERIAL)
                tabLocation.value = level1
                level2Node.value = node2
                popupLiveData.value = value ?: ""
            }
        }
    }

    private fun initTabLayout() {
        with(this) {
            binding.tabLayout.tabMode = TabLayout.MODE_AUTO
            binding.tabLayout.addOnTabSelectedListener(this)
            binding.viewPager.adapter = mAdapter
            binding.viewPager.isUserInputEnabled = false
            // ?????????????????????
//            binding.viewPager.offscreenPageLimit = 1
            val childAt: RecyclerView = binding.viewPager.getChildAt(0) as RecyclerView
            childAt.setItemViewCacheSize(0)
            childAt.layoutManager?.isItemPrefetchEnabled = false
            TabLayoutMediator(
                binding.tabLayout, binding.viewPager, true, false
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = mAdapter.getPageTitle(position)
                tab.view.isLongClickable = false
                tab.view.tooltipText = null
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
        if (!isShow) {
            mAdapter.clear()
            return
        }
        var values = TabPage.values()
        if (VcuUtils.isCareLevel(Level.LEVEL3, expect = true)) {
            values = values.dropWhile { it == TabPage.COMMONLY }.toTypedArray()
        }
        // ???????????????????????????
        values.forEach {
            when (it) {
                TabPage.COMMONLY -> {
                    val fragment = CommonlyFragment::class.java
                    //                    fragment.userVisibleHint = true
                    mAdapter.addFragment(fragment, resources.getString(it.desc))
                }
                TabPage.ACCESS -> {
                    val fragment = DoorsManageFragment::class.java
                    mAdapter.addFragment(fragment, resources.getString(it.desc))
                }
                TabPage.LIGHTING -> {
                    val fragment = LightingManageFragment::class.java
                    mAdapter.addFragment(fragment, resources.getString(it.desc))
                }
                TabPage.SOUND -> {
                    val fragment = SoundManageFragment::class.java
                    mAdapter.addFragment(fragment, resources.getString(it.desc))
                }
                TabPage.COCKPIT -> {
                    val fragment = CabinManagerFragment::class.java
                    mAdapter.addFragment(fragment, resources.getString(it.desc))
                }
                TabPage.ADAS -> {
                    val fragment = DriveManageFragment::class.java
                    mAdapter.addFragment(fragment, resources.getString(it.desc))
                }
            }
        }
        binding.viewPager.offscreenPageLimit = mAdapter.itemCount
        mAdapter.notifyDataSetChanged()
        binding.viewPager.setCurrentItem(tabLocation.value!!, true)
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
            if (tab.text?.equals("??????") == true) {
                binding.constraint.setBackgroundResource(R.drawable.bg_changyong_1920)
            } else if (tab.text?.equals("????????????") == true) {
                binding.constraint.setBackgroundResource(R.drawable.bg)
            } else {
                binding.constraint.setBackgroundResource(R.drawable.right_bg)
            }
            if (tab.text == "BEIJING OS") {
                doRouteToUpgrade()
            }
        }
        if (firstCreate) {
            firstCreate = false
            return
        }
        tabLocation.postValue(binding.tabLayout.selectedTabPosition)
    }

    private fun doRouteToUpgrade() {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            val componentName =
                ComponentName("com.hmi.beic62.pc", "com.hmi.beic62.pc.FMainActivity")
            intent.setComponent(componentName)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("type", "from downbar2022") //??????Intent??????
            startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e.message)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onDestroy() {
        manager.setTabSerial(tabLocation.value!!)
        super.onDestroy()
    }

    override fun obtainLevelLiveData(): LiveData<Node> {
        return level2Node
    }

    override fun obtainPopupLiveData(): LiveData<String> {
        return popupLiveData
    }

    override fun cleanPopupLiveDate(serial: String): Boolean {
        if (serial == popupLiveData.value) {
            popupLiveData.value = ""
            return true
        }
        return false
    }

    override fun resetLevelRouter(lv1: Int, lv2: Int, lv3: Int) {
        TODO("Not yet implemented")
    }

}