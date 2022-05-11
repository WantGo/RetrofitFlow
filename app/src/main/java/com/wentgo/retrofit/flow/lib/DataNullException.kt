package com.wentgo.retrofit.flow.lib

import androidx.annotation.Keep

/**
 * @author wentgo
 * @version 1.0
 * @time 2022/04/14
 * @description 数据为空异常
 */
@Keep
class DataNullException : Exception("Data is null")