package com.chinatsp.vehicle.settings.fragment

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.databinding.AccessFragmentBinding
import com.common.library.frame.base.BaseTabFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommonlyFragment: BaseTabFragment<BaseViewModel, AccessFragmentBinding>() {

    override val nodeId: Int
        get() = 0

    override val tabLocation: MutableLiveData<Int> by lazy { MutableLiveData(0) }


    override fun getLayoutId(): Int {
        return R.layout.access_fragment
    }

    override fun initData(savedInstanceState: Bundle?) {

    }


}