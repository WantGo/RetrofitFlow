package com.wentgo.retrofit.flow.lib

import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.CallAdapter

import java.lang.reflect.Type

/**
 * @author wentgo
 * @date 2022/3/19
 * @description  FLow<BaseEntity<T>>转换程 FLow<T>
 */
class FlowBaseEntityCallAdapter<T>(private val responseType: Type) :
    CallAdapter<BaseEntity<T>, Flow<T>> {
    override fun responseType() = responseType.toBaseEntityType()
    override fun adapt(call: Call<BaseEntity<T>>): Flow<T> {
        return call.toEntityFlow()
    }
}