package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.lamp.LampManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.LightingManageFragmentBinding
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LightingManageFragment : BaseFragment<BaseViewModel, LightingManageFragmentBinding>() {

    private lateinit var tabOptions: List<View>

    private val manager: LampManager
        get() = LampManager.instance

    private val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(manager.getTabSerial()) }

    override fun getLayoutId(): Int {
        return R.layout.lighting_manage_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initTabOptions()
        tabLocation.observe(this) {
            updateSelectTabOption(it)
        }
        tabLocation.let {
            if (it.value == -1) {
                it.value = R.id.lighting_tab
            } else {
                it.value = it.value
            }
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
    }

    private fun updateSelectTabOption(viewId: Int) {
        tabOptions.forEach { it.isSelected = false }
        updateDisplayFragment(viewId)

    }

    private fun updateDisplayFragment(serial: Int) {
        var fragment: Fragment? = checkOutFragment(serial)
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
        var fragment: Fragment? = null
        when (serial) {
            R.id.lighting_tab -> {
                fragment = LightingFragment()
            }
            R.id.lighting_atmosphere -> {
                fragment = AmbientLightingFragment()
            }
            R.id.lighting_screen -> {
                fragment = LightingScreenFragment()
            }
            else -> {
            }
        }
        return fragment
    }
}