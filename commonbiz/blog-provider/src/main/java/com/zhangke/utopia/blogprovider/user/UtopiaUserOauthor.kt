package com.zhangke.utopia.blogprovider.user

/**
 * Created by ZhangKe on 2022/12/23.
 */
interface UtopiaUserOauthor<T> {

    suspend fun oauth(): UtopiaUser<T>
}