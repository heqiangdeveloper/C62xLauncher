package com.chinatsp.vehicle.settings.fragment.sound

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import com.chinatsp.settinglib.manager.sound.AudioManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.SoundManageFragmentBinding
import com.common.library.frame.base.BaseTabFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SoundManageFragment : BaseTabFragment<BaseViewModel, SoundManageFragmentBinding>() {

    private lateinit var tabOptions: List<View>

    private val manager: AudioManager
        get() = AudioManager.instance

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(manager.getTabSerial()) }

    override fun getLayoutId(): Int {
        return R.layout.sound_manage_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {
        initTabOptions()
        tabLocation.observe(this) {
            updateSelectTabOption(it)
        }
        tabLocation.let {
            if (it.value == -1) {
                it.value = R.id.sound_tab
            } else {
                it.value = it.value
            }
        }
    }

    private fun onClick(view: View) {
        tabLocation.takeIf { it.value != view.id }?.value = view.id
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