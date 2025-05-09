package com.zhangke.fread.common.update

expect class AppPlatformUpdater() {

    val platformName: String

    fun getAppVersionCode(): Long

    fun triggerUpdate(releaseInfo: AppReleaseInfo)
}
