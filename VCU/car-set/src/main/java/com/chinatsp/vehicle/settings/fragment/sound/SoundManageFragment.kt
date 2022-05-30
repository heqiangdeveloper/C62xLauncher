package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.databinding.SoundManageFragmentBinding
import com.chinatsp.vehicle.settings.fragment.lighting.LightingAtmosphereFragment
import com.chinatsp.vehicle.settings.fragment.lighting.LightingFragment
import com.chinatsp.vehicle.settings.vm.SoundViewModel
import com.common.library.frame.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundManageFragment : BaseFragment<SoundViewModel, SoundManageFragmentBinding>() {
    var selectOption: View? = null
    private lateinit var tabOptions: List<View>

    override fun getLayoutId(): Int {
        return R.layout.sound_manage_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initTabOptions()
        viewModel.tabLocationLiveData.observe(this) {
            updateSelectTabOption(it)
        }
        viewModel.tabLocationLiveData.let {
            if (it.value == -1) {
                it.value = R.id.sound_tab
            }
        }
    }

    private fun onClick(view: View) {
        if (view != selectOption) {
            viewModel.tabLocationLiveData.let {
                it.value = view.id
            }
        }
    }

    private fun initTabOptions() {
        val tabOptionLayout = binding.soundManagerLeftTab
        val range = 0 until tabOptionLayout.childCount
        tabOptions = range.map {
            val child = tabOptionLayout.getChildAt(it)
            child.apply { setOnClickListener { onClick(this) } }
            child
        }.toList()
    }

    private fun updateSelectTabOption(viewId: Int) {
        if (viewId != selectOption?.id) {
            selectOption?.isEnabled = true
            selectOption = tabOptions.first { it.id == viewId }
            selectOption?.isEnabled = false
            updateDisplayFragment(viewId)
        }
    }

    private fun updateDisplayFragment(serial: Int) {
        var fragment: Fragment? = checkOutFragment(serial)
        fragment?.let {
            val manager: FragmentManager = childFragmentManager
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.replace(R.id.sound_manager_layout, it, it::class.simpleName)
            transaction.commitAllowingStateLoss()
        }
    }

    private fun checkOutFragment(serial: Int): Fragment? {
        var fragment: Fragment? = null
        when (serial) {
            R.id.sound_tab -> {
                fragment = SoundFragment()
            }
            R.id.sound_effect -> {
                fragment = SoundEffectFragment()
            }
            else -> {
            }
        }
        return fragment
    }
}