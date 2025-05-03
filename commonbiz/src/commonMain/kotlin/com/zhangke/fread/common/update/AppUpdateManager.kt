package com.zhangke.fread.common.update

import com.zhangke.framework.architect.http.sharedHttpClient
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.common.config.FreadConfigManager
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.takeFrom
import me.tatarka.inject.annotations.Inject

class AppUpdateManager @Inject constructor(
    private val configManager: FreadConfigManager,
) {

    companion object {

        private const val API_RELEASE = "https://api.fread.xyz/app/release"
        private const val QUERY_PLATFORM = "platform"
    }

    private val platformUpdater = AppPlatformUpdater()

    suspend fun checkForUpdate(checkIgnoreVersion: Boolean = true): Result<Pair<Boolean, AppReleaseInfo>> {
        val releaseInfoResult = getReleaseInfo()
        if (releaseInfoResult.isFailure) return Result.failure(releaseInfoResult.exceptionOrThrow())
        val releaseInfo = releaseInfoResult.getOrThrow()
        val currentVersion = platformUpdater.getAppVersionCode()
        if (releaseInfo.versionCode <= currentVersion) {
            return Result.success(false to releaseInfo)
        }
        if (checkIgnoreVersion) {
            val ignoreUpdateVersion = configManager.getIgnoreUpdateVersion() ?: -1
            if (ignoreUpdateVersion >= releaseInfo.versionCode) {
                // ignore this version
                return Result.success(false to releaseInfo)
            }
        }
        return Result.success(true to releaseInfo)
    }

    suspend fun updateApp(releaseInfo: AppReleaseInfo) {
        platformUpdater.triggerUpdate(releaseInfo)
    }

    suspend fun ignoreVersion(releaseInfo: AppReleaseInfo) {
        configManager.updateIgnoreUpdateVersion(releaseInfo.versionCode)
    }

    private suspend fun getReleaseInfo(): Result<AppReleaseInfo> {
        return runCatching {
            sharedHttpClient.get {
                url {
                    takeFrom(API_RELEASE)
                    parameter(QUERY_PLATFORM, platformUpdater.platformName)
                }
            }.body<AppReleaseInfo>()
        }
    }
}
