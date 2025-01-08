package com.zhangke.fread.common.update

import android.app.DownloadManager
import android.os.Environment
import androidx.core.net.toUri
import com.zhangke.framework.utils.SystemPageUtils
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.common.update.AppReleaseInfo

actual class AppPlatformUpdater {

    actual val platformName: String = "Android"

    actual fun getAppVersionCode(): Long {
        return appContext.packageManager
            .getPackageInfo(appContext.packageName, 0)
            .versionCode
            .toLong()
    }

    actual fun triggerUpdate(releaseInfo: AppReleaseInfo) {
        if (SystemPageUtils.openAppMarket(appContext)){
            return
        }
        val downloadManager = appContext.getSystemService(DownloadManager::class.java)
        val request = DownloadManager.Request(releaseInfo.downloadUrl.toUri())
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle("Fread ${releaseInfo.versionName} apk")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "fread_${releaseInfo.versionName}.apk")
        val downloadId = downloadManager.enqueue(request)
    }
}
