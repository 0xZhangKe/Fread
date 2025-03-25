package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion
import app.bsky.actor.SavedFeed
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
                    preference
                } else {
                    preference
                }
            }
        }
    }

//    private fun reorder(list: List<SavedFeed>, newOrder: List<BlueskyFeeds>): List<SavedFeed> {
//        newOrder.map {
//
//        }
//    }
}
