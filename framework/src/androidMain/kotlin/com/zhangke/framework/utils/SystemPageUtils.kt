package com.zhangke.framework.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object SystemPageUtils {

    fun openAppMarket(context: Context) {
        val uri = Uri.parse("market://details?id=${context.packageName}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val activity = context.extractActivity()
        if (activity == null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            if (activity == null) {
                context.startActivity(intent)
            } else {
                activity.startActivity(intent)
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Application market not found", Toast.LENGTH_SHORT).show()
        }
    }
}
