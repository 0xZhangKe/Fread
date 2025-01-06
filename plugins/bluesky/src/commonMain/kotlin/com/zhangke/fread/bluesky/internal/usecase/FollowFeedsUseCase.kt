package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion.SavedFeedsPrefV2
import app.bsky.actor.PutPreferencesRequest
import app.bsky.actor.SavedFeed
import app.bsky.actor.Type
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.collections.immutable.toImmutableList
import me.tatarka.inject.annotations.Inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FollowFeedsUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        feeds: BlueskyFeeds.Feeds,
    ): Result<Unit> {
        val client = clientManager.getClient(role)
        val preferenceResult = client.getPreferencesCatching()
        if (preferenceResult.isFailure) {
            return Result.failure(preferenceResult.exceptionOrThrow())
        }
        val preference = preferenceResult.getOrThrow()
        val request = PutPreferencesRequest(
            preferences = preference.preferences.map {
                if (it is SavedFeedsPrefV2) {
                    val newItems = it.value.items + feeds.toSaveFeeds()
                    SavedFeedsPrefV2(app.bsky.actor.SavedFeedsPrefV2(newItems.toImmutableList()))
                } else {
                    it
                }
            }.toImmutableList(),
        )
        return clientManager.getClient(role).putPreferencesCatching(request)
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun BlueskyFeeds.Feeds.toSaveFeeds(): SavedFeed {
        return SavedFeed(
            id = Uuid.random().toString(),
            type = Type.FEED,
            value = this.uri,
            pinned = true,
        )
    }
}
