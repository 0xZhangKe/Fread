package com.zhangke.utopia.status.auth

/**
 * description a logged user.
 *
 * Created by ZhangKe on 2022/12/22.
 */
open class UtopiaUser(
    /**
     * Where this user belong to.
     */
    val domain: String,

    val name: String,

    val id: String,

    val avatar: String?,

    val description: String?,

    val homePage: String?,

    val selected: Boolean,
)