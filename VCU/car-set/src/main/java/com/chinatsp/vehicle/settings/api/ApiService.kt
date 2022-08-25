package com.chinatsp.vehicle.settings.api

import com.chinatsp.vehicle.settings.bean.OilPrice
import com.chinatsp.vehicle.settings.bean.Result
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 */
interface ApiService {

    /**
     * 查询国内油价
     * @return
     */
    @GET("gnyj/query")
    fun getOilPriceInfo(@Query("key") key: String): Call<Result<List<OilPrice>>>
}