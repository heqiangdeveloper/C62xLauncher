package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.VcuUtils
import com.chinatsp.settinglib.manager.lamp.LampManager
import com.chinatsp.vehicle.controller.annotation.Level
import com.chinatsp.vehicle.settings.IRoute
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.LightingManageFragmentBinding
import com.common.library.frame.base.BaseFragment
import com.chinatsp.vehicle.settings.fragment.BaseTabFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LightingManageFragment : BaseTabFragment<BaseViewModel, LightingManageFragmentBinding>() {

    private val manager: LampManager
        get() = LampManager.instance

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(manager.getTabSerial()) }

    override fun getLayoutId(): Int {
        return R.layout.lighting_manage_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initTabOptions()
        initTabLocation()
    }

    private fun obtainRouter(): IRoute? {
        return if (activity is IRoute) activity as IRoute else null
    }

    private fun initTabLocation() {
        tabLocation.let {
            it.observe(this) { location ->
                updateSelectTabOption(location)
            }
            initTabLocation(it, R.id.lighting_tab)
        }
    }

    private fun initTabLocation(it: MutableLiveData<Int>, default: Int) {
        if (it.value == -1) {
            it.value = default
        } else {
            it.value = it.value
        }
    }

    private fun onClick(view: View) {
        tabLocation.takeIf { it.value != view.id }?.value = view.id
    }

    private fun initTabOptions() {
        val tabOptionLayout = binding.lightingManagerLeftTab
        val range = 0 until tabOptionLayout.childCount
        tabOptions = range.map {
            val child = tabOptionLayout.getChildAt(it)
            child.apply { setOnClickListener { onClick(this) } }
            child
        }.toList()
        initRouteListener()
    }

    private fun updateSelectTabOption(viewId: Int) {
        tabOptions.forEach { it.isSelected = false }
        updateDisplayFragment(viewId)

    }

    private fun updateDisplayFragment(serial: Int) {
        val fragment: Fragment? = checkOutFragment(serial)
        tabOptions.first { it.id == serial }.isSelected = true
        manager.setTabSerial(serial)
        fragment?.let {
            val manager: FragmentManager = childFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.replace(R.id.lighting_manager_layout, it, it::class.simpleName)
            transaction.commitAllowingStateLoss()
        }
    }

    private fun checkOutFragment(serial: Int): Fragment? {
        var fragment: BaseFragment<out BaseViewModel, out ViewDataBinding>? = null
        when (serial) {
            R.id.lighting_tab -> {
                binding.constraint.setBackgroundResource(R.drawable.right_bg)
                fragment = LightingFragment()
                fragment.pid = uid
                fragment.uid = 1
            }
            R.id.lighting_atmosphere -> {
                if (VcuUtils.isCareLevel(Level.LEVEL3, expect = true)) {
                    binding.constraint.setBackgroundResource(R.drawable.intelligent_model_lv3)
                } else {
                    binding.constraint.setBackgroundResource(R.drawable.intelligent_model_lv4_5)
                }
                fragment = AmbientLightingFragment()
                fragment.pid = uid
                fragment.uid = 2
            }
            R.id.lighting_screen -> {
                binding.constraint.setBackgroundResource(R.drawable.right_bg)
                fragment = LightingScreenFragment()
                fragment.pid = uid
                fragment.uid = 3
            }
            else -> {
            }
        }
        return fragment
    }

    private fun initRouteListener() {
        val router = obtainRouter()
        if (null != router) {
            val liveData = router.obtainLevelLiveData()
            liveData.observe(this) {
                syncRouterLocation(it)
            }
        }
    }

    override fun resetRouter(lv1: Int, lv2: Int, lv3: Int) {
        obtainRouter()?.resetLevelRouter(lv1, lv2, lv3)
    }
}