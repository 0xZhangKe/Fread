package com.zhangke.fread.activitypub.app.internal.push.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.common.action.OpenNotificationPageAction
import kotlin.random.Random


class PushNotificationManager {

    companion object {

        private const val CHANNEL_ID = "InteractionMessages"
    }

    fun onReceiveNewMessage(context: Context, message: FcmPushMessage) {
        if (!checkSelfPushPermission(context)) return
        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.zhangke.fread.commonbiz.R.drawable.ic_fread_logo)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(buildNotificationIntent(context, message))
        val notificationId = Random.nextInt(0, Int.MAX_VALUE)
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    private fun buildNotificationIntent(context: Context, message: FcmPushMessage): PendingIntent {
        val openNavigationUri = OpenNotificationPageAction.buildOpenNotificationPageRoute()
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)!!
        intent.data = openNavigationUri.toUri()
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun checkSelfPushPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Interaction messages",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Notification for interaction messages"
        }
        context.pusManager.createNotificationChannel(channel)
    }

    private val Context.pusManager: NotificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}
