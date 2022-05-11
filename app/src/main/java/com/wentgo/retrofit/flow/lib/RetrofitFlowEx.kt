package com.wentgo.retrofit.flow.lib

import android.annotation.SuppressLint
import android.util.Log
import com.wentgo.retrofit.flow.BuildConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type

/**
 * @author wentgo
 * @version 1.0
 * @time 2022/03/18
 * @description retrofit 协程扩展
 */
/**
 * 封装结果
 */
class RetrofitResult<T>(var data: T? = null, var throwable: Throwable? = null)

/**
 * 错误回调
 */
fun <T> RetrofitResult<T>.onFail(block: (Throwable) -> Unit): RetrofitResult<T> {
    this.throwable?.let { block.invoke(it) }
    return this
}

/**
 * 数据为Null的时候调用这个会触发回调
 */
fun <T> RetrofitResult<T>.onDataNull(block: () -> Unit): RetrofitResult<T> {
    if (this.throwable is DataNullException) block.invoke()
    return this
}

/**
 *直接获取result
 */
suspend fun <T> Flow<T>.result(): T? {
    return onSuccessOrNull().data
}

/**
 *结果回调,可以为空
 */
suspend fun <T> Flow<T>.onSuccessOrNull(block: ((T?) -> Unit)? = null): RetrofitResult<T> {
    return this.getDataOrNull(true, block)
}

/**
 *结果回调,可以为空
 */
suspend fun <T> Flow<T>.getDataOrNull(
    allowNull: Boolean = true,
    block: ((T?) -> Unit)? = null
): RetrofitResult<T> {
    return RetrofitResult<T>().also { result ->
        kotlin.runCatching {
            result.data = firstOrNull()
            block?.invoke(result.data)
        }
            .onFailure {
                if (allowNull && it is DataNullException) {
                    retrofitLogD(it.message ?: "")
                    block?.invoke(null)
                } else {
                    retrofitLogE(throwable = it)
                    result.throwable = if (it is RetrofitApiException) it.apiException else it
                }
            }
    }
}

/**
 *结果回调，结果不能为空
 */
suspend fun <T> Flow<T>.onSuccess(block: (T) -> Unit): RetrofitResult<T> {
    return getDataOrNull(false) {
        it?.let { block.invoke(it) }
    }
}

/**
 * 如果使用Suspend获取的数据可以用这个
 */
fun <T> BaseEntity<T>.toData(): T {
    return this.data
}

/**
 * T 转换成 BaseEntity<T>
 */
fun Type.toBaseEntityType(): Type {
    return ParameterizedTypeImpl(null, BaseEntity::class.java, this)
}

/**
 * 解析数据用
 * @see FlowCallAdapterFactory
 */
fun <T> Call<T>.toResponseFlow(): Flow<Response<T>> {
    return channelFlow<Response<T>> {
        send(execute().checkError())
        close()//主动关闭Flow，否则会内存泄漏
    }.flowOn(Dispatchers.IO).onCompletion { this@toResponseFlow.cancel() }
}

/**
 * 解析数据用
 *  @see FlowCallAdapterFactory
 */
fun <T> Call<T>.mapEntity(): Flow<T> {
    return toResponseFlow().map { it.body() ?: throw ResponseNullException() }
}

/**
 * 解析数据用
 *  @see FlowCallAdapterFactory
 *  @see FlowBaseEntityCallAdapter
 */
fun <T> Call<BaseEntity<T>>.toEntityFlow(): Flow<T> {
    return mapEntity().map {
        val url = this@toEntityFlow.request().url.toString()
        if (it.success) {
            retrofitLogD("${url}-> 请求成功")
            it.data ?: throw DataNullException()
        } else throw RetrofitApiException(url, ApiException(it.msg))
    }
}

/**
 * 检查错误
 */
fun <T> Response<T>.checkError(): Response<T> {
    checkNotNull(this) {
        "response is null"
    }
    if (isSuccessful.not()) {
        throw HttpException(this)
    }
    return this
}


@SuppressLint("LogNotTimber")
fun retrofitLogE(tag: String? = null, throwable: Throwable) {
    if (BuildConfig.DEBUG) {
        when (throwable) {
            is HttpException -> {
                Log.e(
                    tag ?: "retrofit",
                    "${throwable.response()?.raw()?.request?.url} 网络请求异常",
                    throwable
                )
            }
            is RetrofitApiException -> {
                Log.e(
                    tag ?: "retrofit",
                    "${throwable.url} API异常",
                    throwable.apiException
                )
            }
            else -> {
                Log.e(
                    tag ?: "retrofit",
                    "Retrofit 未知异常",
                    throwable
                )
            }
        }
    }
}


fun <T> Class<T>.createApi(): T {
    return RetrofitManager.client!!.create(this)
}

fun retrofitLogD(msg: String) {
    if (BuildConfig.DEBUG) {
        Log.d("retrofit", msg)
    }
}

