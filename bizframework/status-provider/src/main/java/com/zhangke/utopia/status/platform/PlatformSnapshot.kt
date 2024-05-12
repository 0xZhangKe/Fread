package com.zhangke.utopia.status.platform

data class PlatformSnapshot (
    val domain: String,
    val description: String,
    val version: String,
    val language: String,
    val thumbnail: String,
    val totalUsers: Int,
    val lastWeekUsers: Int,
    val category: String,
)
