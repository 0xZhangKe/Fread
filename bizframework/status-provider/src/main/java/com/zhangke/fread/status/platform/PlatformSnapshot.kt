package com.zhangke.fread.status.platform

import com.zhangke.fread.status.model.StatusProviderProtocol

data class PlatformSnapshot (
    val domain: String,
    val description: String,
    val version: String,
    val language: String,
    val thumbnail: String,
    val totalUsers: Int,
    val lastWeekUsers: Int,
    val category: String,
    val protocol: StatusProviderProtocol,
)
