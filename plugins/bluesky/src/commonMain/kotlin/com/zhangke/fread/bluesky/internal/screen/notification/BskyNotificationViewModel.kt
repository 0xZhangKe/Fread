package com.zhangke.fread.bluesky.internal.screen.notification

import androidx.lifecycle.ViewModel
import app.bsky.notification.ListNotificationsQueryParams
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.adapter.BlueskyNotificationAdapter
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.usecase.GetCompletedNotificationUseCase
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class BskyNotificationViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val role: IdentityRole,
    private val platformRepo: BlueskyPlatformRepo,
    private val accountAdapter: BlueskyAccountAdapter,
    private val statusAdapter: BlueskyStatusAdapter,
    private val notificationAdapter: BlueskyNotificationAdapter,
    private val getCompletedNotification: GetCompletedNotificationUseCase,
    private val onlyMention: Boolean,
) : ViewModel() {

    private var cursor: String? = null

    private var blogPlatform: BlogPlatform? = null

    private suspend fun loadNotification(cursor: String? = this.cursor): Result<List<StatusNotification>> {
        val platform = getBlogPlatform(clientManager.getClient(role))
        return getCompletedNotification(
            role = role,
            params = ListNotificationsQueryParams(
                reasons = if (onlyMention) listOf("mention", "reply", "quote") else emptyList(),
                cursor = cursor,
            ),
        ).map { paged ->
            this@BskyNotificationViewModel.cursor = paged.cursor
            paged.notifications.map { notificationAdapter.convert(it, platform) }
        }
    }

    private suspend fun getBlogPlatform(client: BlueskyClient): BlogPlatform {
        return blogPlatform ?: platformRepo.getPlatform(client.baseUrl).also { blogPlatform = it }
    }
}
