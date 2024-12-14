package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion.SavedFeedsPrefV2
import app.bsky.actor.SavedFeed
import app.bsky.actor.Type
import app.bsky.feed.GeneratorView
import app.bsky.feed.GetFeedGeneratorsQueryParams
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.collections.immutable.toImmutableList
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri

class GetFeedsUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
) {

    suspend operator fun invoke(role: IdentityRole): Result<List<GeneratorView>> {
        val client = clientManager.getClient(role)
        val preferenceResult = client.getPreferencesCatching()
        if (preferenceResult.isFailure) return Result.failure(preferenceResult.exceptionOrThrow())
        val preference = preferenceResult.getOrThrow()
        val feeds = preference.preferences.filterIsInstance<SavedFeedsPrefV2>()
            .firstOrNull()
            ?.value
            ?.items
            ?.filter { it.type == Type.FEED } ?: emptyList<SavedFeed>()
        if (feeds.isEmpty()) return Result.success(emptyList())
        return client.getFeedGeneratorsCatching(
            GetFeedGeneratorsQueryParams(
                feeds = feeds.map { AtUri(it.value) }.toImmutableList()
            )
        ).map { it.feeds }
    }
}
