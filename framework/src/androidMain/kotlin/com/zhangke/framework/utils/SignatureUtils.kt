package com.zhangke.framework.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import okio.ByteString

@OptIn(ExperimentalStdlibApi::class)
fun Context.getApkSignatureSha1(): String? {
    val signature = getApkSignatures().firstOrNull() ?: return null
    val signatureByteString = ByteString.of(*signature.toByteArray())
    val sha1 = signatureByteString.sha1().toByteArray().toHexString(
        HexFormat {
            upperCase = true
            bytes.bytesPerGroup = 1
            bytes.groupSeparator = ":"
        },
    )
    return sha1
}

private fun Context.getApkSignatures(): List<Signature> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                ?.signingInfo?.apkContentsSigners?.mapNotNull { it } ?: emptyList()
        } catch (_: Throwable) {
            emptyList()
        }
    } else {
        try {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                ?.signatures?.mapNotNull { it } ?: emptyList()
        } catch (_: Throwable) {
            emptyList()
        }
    }
}
