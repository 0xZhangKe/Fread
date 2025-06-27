package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion.SavedFeedsPrefV2
import app.bsky.actor.SavedFeed
import app.bsky.actor.Type
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PinFeedsUseCase @Inject constructor(
    private val updatePreferences: UpdatePreferencesUseCase,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        feeds: BlueskyFeeds.Feeds,
    ): Result<Unit> {
        return updatePreferences(
            locator = locator,
            updater = { preference ->
                preference.map {
                    if (it is SavedFeedsPrefV2) {
                        val newItems = it.value.items + feeds.toSaveFeeds()
                        SavedFeedsPrefV2(app.bsky.actor.SavedFeedsPrefV2(newItems))
                    } else {
                        it
                    }
                }
            },
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun BlueskyFeeds.Feeds.toSaveFeeds(): SavedFeed {
        return SavedFeed(
            id = Uuid.random().toString(),
            type = Type.Feed,
            value = this.uri,
            pinned = true,
        )
    }
}
