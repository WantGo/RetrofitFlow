package com.wentgo.retrofit.flow.lib

import androidx.annotation.Keep
import java.io.IOException

/**
 * @author wentgo
 * @version 1.0
 * @time 2022/04/06
 * @description
 */
@Keep
class RetrofitApiException(val url: String, val apiException: ApiException) : IOException()