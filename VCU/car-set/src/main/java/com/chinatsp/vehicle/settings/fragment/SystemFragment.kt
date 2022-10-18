package com.chinatsp.vehicle.settings.fragment

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.FragmentSimpleTabBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SystemFragment : BaseTabFragment<BaseViewModel, FragmentSimpleTabBinding>() {
    

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(0) }


    override fun getLayoutId(): Int {
        return R.layout.fragment_simple_tab
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun resetRouter(lv1: Int, lv2: Int, lv3: Int) {

    }


}