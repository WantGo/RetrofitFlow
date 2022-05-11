package com.wentgo.retrofit.flow.lib

import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.IllegalStateException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author wentgo
 * @date 2022/3/20
 * @description Retrofit支持Flow类型
 * Retrofit官方没有支持Flow转换器，仿造RxJava2CallAdapterFactory写的
 * 只支持3种格式 Flow<T>,Flow<Response<T>> Flow<BaseEntity<T>>
 * Flow<T> 是在 Flow<BaseEntity<T>> 解析完成后再解析，如果遇到api异常，会直接抛出异常
 */
class FlowCallAdapterFactory private constructor(
) : CallAdapter.Factory() {
    override fun get(
        returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rawType = getRawType(returnType)
        if (rawType != Flow::class.java) {
            //不是Flow<T>返回空
            return null
        }
        //如果只是Flow 没有T也不行
        check(returnType is ParameterizedType) {
            val name = "Flow"
            throw IllegalStateException(
                "Flow return type must be parameterized"
                        + " as " + name + "<Foo> or " + name + "<? extends Foo>"
            )
        }
        val flowDataType = getParameterUpperBound(0, returnType)
        when (getRawType(flowDataType)) {
            // 返回类型是Response<T>
            Response::class.java -> {
                check(flowDataType is ParameterizedType) {
                    ("Response must be parameterized"
                            + " as Flow<Response<T>> or Flow<Response<? extends T>>")
                }
                return FlowResponseCallAdapter<Any>(
                    getParameterUpperBound(
                        0,
                        flowDataType
                    )
                )
            }
            BaseEntity::class.java -> {
                check(flowDataType is ParameterizedType) {
                    ("Response must be parameterized"
                            + " as Flow<BaseEntity<T>> or Flow<BaseEntity<? extends T>>")
                }
                return FlowBodyCallAdapter<Any>(flowDataType)
            }
            else -> {
                return FlowBaseEntityCallAdapter<Any>(flowDataType)
            }
        }
    }

    companion object {
        fun create(): FlowCallAdapterFactory {
            return FlowCallAdapterFactory()
        }
    }
}