package com.zhangke.fread.common.update

data class AppReleaseInfo(
    val versionCode: Long,
    val versionName: String,
    val releaseNote: String,
    val downloadUrl: String,
)
