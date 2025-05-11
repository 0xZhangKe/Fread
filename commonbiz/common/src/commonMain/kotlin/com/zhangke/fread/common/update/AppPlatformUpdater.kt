package com.zhangke.fread.common.update

expect class AppPlatformUpdater() {

    val platformName: String

    val signingForFDroid: Boolean

    fun getAppVersionCode(): Long

    fun triggerUpdate(releaseInfo: AppReleaseInfo)
}
