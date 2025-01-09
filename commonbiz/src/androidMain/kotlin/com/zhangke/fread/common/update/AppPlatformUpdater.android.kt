package com.zhangke.fread.common.update

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.zhangke.framework.activity.TopActivityManager
import com.zhangke.framework.utils.SystemPageUtils
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.common.utils.registerActivityResultCallback

actual class AppPlatformUpdater {

    companion object {

        private const val REQUEST_INSTALL_PERMISSION_CODE = 1002928
    }

    actual val platformName: String = "android"

    actual fun getAppVersionCode(): Long {
        return appContext.packageManager
            .getPackageInfo(appContext.packageName, 0)
            .versionCode
            .toLong()
    }

    actual fun triggerUpdate(releaseInfo: AppReleaseInfo) {
        if (SystemPageUtils.openAppMarket(appContext)) return
        val downloadManager = appContext.getSystemService(DownloadManager::class.java)
        val request = DownloadManager.Request(releaseInfo.downloadUrl.toUri())
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle("Fread ${releaseInfo.versionName} apk")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "fread_${releaseInfo.versionName}.apk"
            )
        val downloadId = downloadManager.enqueue(request)
        registerDownloadSuccessReceiver(downloadId) { uri ->
            installApk(uri)
        }
    }

    private fun registerDownloadSuccessReceiver(
        downloadId: Long,
        onDownloadSuccess: (String) -> Unit,
    ) {
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        val receiver = createDownloadSuccessReceiver(downloadId, onDownloadSuccess)
        ContextCompat.registerReceiver(
            appContext,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED,
        )
    }

    private fun createDownloadSuccessReceiver(
        downloadApkId: Long,
        onDownloadSuccess: (String) -> Unit,
    ): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId != downloadApkId) return
                if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                    queryDownloadUri(downloadId)?.let {
                        onDownloadSuccess(it)
                    }
                    appContext.unregisterReceiver(this)
                }
            }
        }
        return receiver
    }

    private fun queryDownloadUri(downloadId: Long): String? {
        val downloadManager = appContext.getSystemService(DownloadManager::class.java)
        return downloadManager.getUriForDownloadedFile(downloadId).toString()
    }

    private fun installApk(uri: String) {
        val activity = TopActivityManager.topActiveActivity ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.packageManager.canRequestPackageInstalls()) {
                activity.registerActivityResultCallback(REQUEST_INSTALL_PERMISSION_CODE) { resultCode, _ ->
                    if (activity.packageManager.canRequestPackageInstalls()) {
                        installApk(uri)
                    }
                }
                openInstallPackagePermissionSettingPage(activity)
                return
            }
        }
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri.toUri(), "application/vnd.android.package-archive")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        runCatching { activity.startActivity(intent) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openInstallPackagePermissionSettingPage(activity: Activity) {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            .setData(Uri.parse("package:${appContext.packageName}"))
        activity.startActivityForResult(intent, REQUEST_INSTALL_PERMISSION_CODE)
    }
}
