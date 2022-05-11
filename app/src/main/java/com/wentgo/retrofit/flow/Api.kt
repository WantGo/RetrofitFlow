package com.wentgo.retrofit.flow


import kotlinx.coroutines.flow.Flow
import retrofit2.http.*
import java.util.*

/**
 * @author [tql](emperor@kucoin.com")
 * @version 3.4
 * @description
 * @time 2020-02-16
 */
interface Api {

    /**
     * 创建价格预警
     *
     * @param entity
     * @return
     */
    @GET("banner/json")
    fun getBannerList(): Flow<List<Banner>>


}
