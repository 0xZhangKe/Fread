package com.zhangke.utopia.blogprovider.user

import java.io.Serializable

/**
 * description a logged user.
 *
 * Created by ZhangKe on 2022/12/22.
 */
class UtopiaUser<T: Serializable>(
    /**
     * Where this user belong to.
     */
    val domain: String,

    val name: String,

    val id: String,

    val token: T,

    val avatar: String?,

    val description: String?,

    val homePage: String?,
)