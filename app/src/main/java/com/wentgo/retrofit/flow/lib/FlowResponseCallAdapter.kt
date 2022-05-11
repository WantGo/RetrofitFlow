package com.wentgo.retrofit.flow.lib

import kotlinx.coroutines.flow.Flow
import retrofit2.*
import java.lang.reflect.Type

/**
 * @author Lingko
 * @date 2022/3/19
 * @description  转换成  FLow<Response<T>>
 */
class FlowResponseCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Flow<Response<T>>> {
    override fun adapt(call: Call<T>): Flow<Response<T>> {
        return call.toResponseFlow()
    }

    override fun responseType() = responseType
}


