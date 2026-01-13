package com.zhangke.framework.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

object SystemPageUtils {

    fun openAppMarket(context: Context): Boolean {
        val uri = "market://details?id=${context.packageName}"
        if (!openSystemViewPage(context, uri)) {
            Toast.makeText(context, "Application market not found", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun openSystemViewPage(
        context: Context,
        uri: String,
    ): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
        val activity = context.extractActivity()
        if (activity == null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            if (activity == null) {
                context.startActivityCompat(intent)
            } else {
                activity.startActivityCompat(intent)
            }
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}
