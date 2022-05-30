package com.chinatsp.vehicle.settings.app.oil

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.common.library.frame.base.BaseModel
import com.chinatsp.vehicle.settings.app.Constants
import com.chinatsp.vehicle.settings.app.base.BaseViewModel
import com.chinatsp.vehicle.settings.bean.OilPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.await
import javax.inject.Inject

/**
 * ViewModel 示例（Kotlin实现方式，与Java不同的是使用到了kotlin一些语法糖和协程相关，将公共部分提取到BaseViewModel，代码量大大减少）
 *
 */
@HiltViewModel
class OilPriceViewModel @Inject constructor(application: Application, model: BaseModel) : BaseViewModel(application, model) {


    val oilLiveData by lazy { MutableLiveData<List<OilPrice>>() }

    /**
     * 获取油价信息
     */
    fun getOilPriceInfo() {
        launch {
            val result = apiService.getOilPriceInfo(Constants.OIL_PRICE_KEY).await()
            if (isSuccess(result)) {
                result.data?.let {
                    oilLiveData.value = it
                }
            }
        }
    }

}