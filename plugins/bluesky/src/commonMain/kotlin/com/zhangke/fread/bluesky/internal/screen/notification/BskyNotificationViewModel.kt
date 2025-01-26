package com.zhangke.fread.bluesky.internal.screen.notification

import androidx.lifecycle.ViewModel
import app.bsky.feed.Like
import app.bsky.feed.Repost
import app.bsky.graph.Follow
import app.bsky.notification.ListNotificationsNotification
import app.bsky.notification.ListNotificationsQueryParams
import app.bsky.notification.ListNotificationsReason
import app.bsky.notification.ListNotificationsResponse
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class BskyNotificationViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val role: IdentityRole,
    private val onlyMention: Boolean,
) : ViewModel() {


    private fun loadNotification() {
        launchInViewModel {
            clientManager.getClient(role)
                .listNotificationsCatching(
                    ListNotificationsQueryParams(reasons = listOf("mention", "reply", "quote"))
                ).onSuccess {

                }.onFailure {

                }
        }
    }

    private fun ListNotificationsResponse.convert() {
        notifications.map { it.convert() }
    }

    private fun ListNotificationsNotification.convert() {
        when (this.reason) {
            is ListNotificationsReason.Like -> {
                val like: Like = this.record.bskyJson()
            }

            is ListNotificationsReason.Repost -> {
                val repost: Repost = this.record.bskyJson()
            }

            is ListNotificationsReason.Follow -> {
                val follow: Follow = this.record.bskyJson()

            }

            is ListNotificationsReason.Quote,
            is ListNotificationsReason.Mention,
            is ListNotificationsReason.Reply -> {
                val follow: Follow = this.record.bskyJson()

            }

            is ListNotificationsReason.StarterpackJoined -> {

            }

            is ListNotificationsReason.Unknown -> {

            }
        }
    }
}
