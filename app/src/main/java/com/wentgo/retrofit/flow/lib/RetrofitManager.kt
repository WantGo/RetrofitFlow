package com.wentgo.retrofit.flow.lib

import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author wenggo
 * @version 1.0
 * @time 2022/05/10
 * @description
 */
object RetrofitManager {
    private const val CONNECT_TIME_OUT = 15L
    private const val TIME_OUT = 30L
    var client: Retrofit? = null
    fun init(baseUrl: String) {
        createFlowRetrofit(baseUrl)
    }

    private fun createFlowRetrofit(baseUrl: String): Retrofit {
        return if (client == null) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(FlowCallAdapterFactory.create())
                .build().apply {
                    client = this
                }
        } else {
            client!!
        }
    }

    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
        return builder.build()
    }
}

