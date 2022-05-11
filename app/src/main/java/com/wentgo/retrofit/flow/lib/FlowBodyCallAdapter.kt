package com.wentgo.retrofit.flow.lib


import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

/**
 * @author wentgo
 * @date 2022/3/19
 * @description  转换成  FLow<Base<T>>
 */
class FlowBodyCallAdapter<T>(private val responseType: Type) : CallAdapter<T, Flow<T>> {
    override fun adapt(call: Call<T>): Flow<T> {
        return call.mapEntity()
    }

    override fun responseType() = responseType
}