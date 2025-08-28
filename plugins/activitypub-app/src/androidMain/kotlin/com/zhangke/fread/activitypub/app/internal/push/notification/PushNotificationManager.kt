package com.zhangke.fread.activitypub.app.internal.push.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.seiko.imageloader.imageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.option.SizeResolver
import com.zhangke.framework.imageloader.executeSafety
import com.zhangke.framework.utils.asBitmapOrNull
import com.zhangke.framework.utils.maybe
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.common.action.OpenNotificationPageAction
import com.zhangke.fread.commonbiz.R
import me.tatarka.inject.annotations.Inject
import kotlin.random.Random

class PushNotificationManager @Inject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo
) {

    companion object {

        private const val NOTIFICATION_CHANNEL_ID = "InteractionMessages"
        private const val NOTIFICATION_GROUP_KEY = "fread.xyz.interaction"
    }

    suspend fun onReceiveNewMessage(context: Context, message: ActivityPubPushMessage) {
        if (!checkSelfPushPermission(context)) return
        createNotificationChannel(context)
        val bitmap = downloadIcon(context, message.icon)
        val loggedAccountCount = accountRepo.queryAll().size
        val notificationIconColor =
            context.getColor(R.color.color_logo_background)
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_skeleton)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_fread_logo))
            .setLights(notificationIconColor, 500, 1000)
            .setColor(notificationIconColor)
            .maybe(bitmap != null) {
                it.setLargeIcon(bitmap)
            }
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup(NOTIFICATION_GROUP_KEY)
            .setContentIntent(buildNotificationIntent(context, message))
            .maybe(loggedAccountCount > 1) {
                it.setSubText(message.account?.userName)
            }
            .setShowWhen(true)
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_SOCIAL)
        val notificationId = Random.nextInt(0, Int.MAX_VALUE)
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    private suspend fun downloadIcon(context: Context, iconUrl: String): Bitmap? {
        val request = ImageRequest(iconUrl) {
            size(SizeResolver(100, 100))
            options {
                isBitmap = true
            }
        }
        return context.imageLoader.executeSafety(request).asBitmapOrNull()
    }

    private fun buildNotificationIntent(
        context: Context,
        message: ActivityPubPushMessage
    ): PendingIntent {
        val openNavigationUri = OpenNotificationPageAction.buildOpenNotificationPageRoute()
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)!!
        intent.data = openNavigationUri.toUri()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
            NOTIFICATION_CHANNEL_ID,
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
