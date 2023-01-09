package com.zhangke.utopia.blogprovider

/**
 * Created by ZhangKe on 2022/12/9.
 */
interface StatusProvider {

    suspend fun requestStatuses(): Result<List<Status>>

}