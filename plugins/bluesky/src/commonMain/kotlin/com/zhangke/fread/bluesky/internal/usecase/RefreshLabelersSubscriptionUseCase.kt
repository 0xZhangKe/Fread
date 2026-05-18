package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.actor.PreferencesUnion
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.client.BlueskyLabelersCache

/**
 * Loads the active Bluesky account's labeler subscriptions from
 * `app.bsky.actor.getPreferences` and stashes the DIDs in [BlueskyLabelersCache]
 * so the next HTTP request sends them in `atproto-accept-labelers`.
 *
 * Without this, custom labelers (skywatch.blue, Aegis, etc.) return no
 * labels, only Bluesky's own moderation service does.
 */
class RefreshLabelersSubscriptionUseCase(
    private val clientManager: BlueskyClientManager,
    private val accountManager: BlueskyLoggedAccountManager,
    private val labelersCache: BlueskyLabelersCache,
) {

    suspend operator fun invoke() {
        val account = accountManager.getAllAccount().firstOrNull() ?: return
        val response = clientManager.getClient(account.locator)
            .getPreferencesCatching()
            .getOrNull() ?: return
        val labelers = response.preferences
            .filterIsInstance<PreferencesUnion.LabelersPref>()
            .flatMap { it.value.labelers }
            .map { it.did.did }
        labelersCache.update(labelers)
    }
}
