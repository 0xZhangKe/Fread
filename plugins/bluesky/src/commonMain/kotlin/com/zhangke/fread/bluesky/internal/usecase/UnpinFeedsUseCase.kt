package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion.SavedFeedsPrefV2
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class UnpinFeedsUseCase @Inject constructor(
    private val updatePreferences: UpdatePreferencesUseCase,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        feeds: BlueskyFeeds.Feeds,
    ): Result<Unit> {
        return updatePreferences(
            role = role,
            updater = { preferences ->
                preferences.map { preference ->
                    if (preference is SavedFeedsPrefV2) {
                        val newItems = preference.value.items.filter { it.value != feeds.uri }
                        SavedFeedsPrefV2(app.bsky.actor.SavedFeedsPrefV2(newItems))
                    } else {
                        preference
                    }
                }
            },
        )
    }
}
