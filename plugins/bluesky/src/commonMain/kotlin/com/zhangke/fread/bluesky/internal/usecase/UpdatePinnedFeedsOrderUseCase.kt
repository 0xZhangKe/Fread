package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion
import app.bsky.actor.SavedFeed
import app.bsky.actor.SavedFeedsPrefV2
import app.bsky.actor.Type
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class UpdatePinnedFeedsOrderUseCase @Inject constructor(
    private val updatePreferences: UpdatePreferencesUseCase,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        feeds: List<BlueskyFeeds>,
    ): Result<Unit> {
        return updatePreferences(role) { preferences ->
            preferences.map { preference ->
                if (preference is PreferencesUnion.SavedFeedsPrefV2) {
                    PreferencesUnion.SavedFeedsPrefV2(
                        value = SavedFeedsPrefV2(reorder(preference.value.items, feeds))
                    )
                } else {
                    preference
                }
            }
        }
    }

    private fun reorder(list: List<SavedFeed>, newOrder: List<BlueskyFeeds>): List<SavedFeed> {
        val uriToFeedsMap = mutableMapOf<String, SavedFeed>()
        for (feed in list) {
            uriToFeedsMap[feed.idForReorder] = feed
        }
        return newOrder.map {
            uriToFeedsMap[it.idForReorder]!!
        }
    }

    private val SavedFeed.idForReorder: String
        get() {
            return when (this.type) {
                is Type.Timeline -> "following"
                else -> value
            }
        }

    private val BlueskyFeeds.idForReorder: String?
        get() {
            return when (this) {
                is BlueskyFeeds.List -> uri
                is BlueskyFeeds.Feeds -> uri
                is BlueskyFeeds.FollowingTimeline -> "following"
                else -> null
            }
        }
}
