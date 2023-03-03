package com.zhangke.utopia.status_provider

/**
 * Created by ZhangKe on 2022/12/9.
 */
interface StatusProvider {

    suspend fun requestStatuses(): Result<List<Status>>

}