package com.wentgo.retrofit.flow.lib

import androidx.annotation.Keep

/**
 * @author lingko
 * @version 1.0
 * @time 2022/04/14
 * @description 数据为空异常
 */
@Keep
class ResponseNullException : Exception("Response is null")