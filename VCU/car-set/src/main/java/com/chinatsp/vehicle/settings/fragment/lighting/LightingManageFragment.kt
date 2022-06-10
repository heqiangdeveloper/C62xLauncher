package com.chinatsp.vehicle.settings.fragment.lighting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.chinatsp.settinglib.manager.lamp.LampManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.LightingManageFragmentBinding
import com.chinatsp.vehicle.settings.vm.LightingViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LightingManageFragment : BaseFragment<LightingViewModel, LightingManageFragmentBinding>() {
    var selectOption: View? = null
    private lateinit var tabOptions: List<View>

    override fun getLayoutId(): Int {
        return R.layout.lighting_manage_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initTabOptions()
        viewModel.tabLocationLiveData.observe(this) {
            updateSelectTabOption(it)
        }
        viewModel.tabLocationLiveData.let {
            if (it.value == -1) {
                it.value = R.id.lighting_tab
            } else {
                it.value = it.value
            }
        }
    }

    private fun onClick(view: View) {
        viewModel.tabLocationLiveData.takeIf { it.value != view.id }?.value = view.id
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
        LampManager.instance.setTabSerial(serial)
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
                fragment = LightingAtmosphereFragment()
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