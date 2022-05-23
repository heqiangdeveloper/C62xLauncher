package com.chinatsp.vehicle.settings.app.oil

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.chinatsp.vehicle.settings.R
import com.chinatsp.vehicle.settings.app.adapter.BindingAdapter
import com.chinatsp.vehicle.settings.bean.OilPrice
import com.chinatsp.vehicle.settings.databinding.OilPriceActivityBinding
import com.common.library.frame.base.BaseActivity
import com.king.base.util.ToastUtils
import dagger.hilt.android.AndroidEntryPoint

/**
 *
 */
@AndroidEntryPoint
class OilPriceActivity : BaseActivity<OilPriceViewModel, OilPriceActivityBinding>() {

    private val mAdapter by lazy { BindingAdapter<OilPrice>(context, R.layout.rv_oil_price_item) }

    override fun getLayoutId(): Int {
        return R.layout.oil_price_activity
    }

    override fun initData(savedInstanceState: Bundle?) {
        registerMessageEvent {
            ToastUtils.showToast(this, it)
        }

        with(viewDataBinding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
//            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL,R.drawable.list_divider_8))
            adapter = mAdapter
        }

        viewModel.oilLiveData.observe(this) {
            mAdapter.refreshData(it)
        }

        binding.srl.setOnRefreshListener {
            viewModel.getOilPriceInfo()
        }

        viewModel.getOilPriceInfo()
    }


    override fun showLoading() {
        if (!viewDataBinding.srl.isRefreshing) {
            viewDataBinding.srl.isRefreshing = true
        }
    }

    override fun hideLoading() {
        viewDataBinding.srl.isRefreshing = false
    }
}