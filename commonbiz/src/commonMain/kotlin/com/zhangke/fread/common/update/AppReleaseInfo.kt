package com.zhangke.fread.common.update

import kotlinx.serialization.Serializable

@Serializable
data class AppReleaseInfo(
    val versionCode: Long,
    val versionName: String,
    val releaseNote: String,
    val downloadUrl: String,
)
