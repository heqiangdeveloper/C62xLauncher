package com.chinatsp.vehicle.settings.fragment

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.FragmentSimpleTabBinding
import com.common.library.frame.base.BaseTabFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SystemFragment : BaseTabFragment<BaseViewModel, FragmentSimpleTabBinding>() {

    override val nodeId: Int
        get() = 7

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(0) }


    override fun getLayoutId(): Int {
        return R.layout.fragment_simple_tab
    }

    override fun initData(savedInstanceState: Bundle?) {

    }


}