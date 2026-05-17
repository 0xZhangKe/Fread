package com.zhangke.fread.bluesky

import app.bsky.actor.FeedViewPref
import app.bsky.actor.PreferencesUnion
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.usecase.UpdatePreferencesUseCase
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.preference.FollowingFeedPrefs
import com.zhangke.fread.status.preference.IFeedPreferencesProvider

private const val HOME_FEED_ID = "home"

class BlueskyFeedPreferencesProvider(
    private val clientManager: BlueskyClientManager,
    private val updatePreferences: UpdatePreferencesUseCase,
) : IFeedPreferencesProvider {

    override suspend fun getFollowingFeedPrefs(
        account: LoggedAccount,
    ): Result<FollowingFeedPrefs>? {
        if (account !is BlueskyLoggedAccount) return null
        val client = clientManager.getClient(account.locator)
        val response = client.getPreferencesCatching()
        if (response.isFailure) {
            return Result.failure(response.exceptionOrThrow())
        }
        val pref = response.getOrThrow()
            .preferences
            .filterIsInstance<PreferencesUnion.FeedViewPref>()
            .firstOrNull { it.value.feed == HOME_FEED_ID }
            ?.value
        return Result.success(pref?.toFollowingFeedPrefs() ?: FollowingFeedPrefs())
    }

    override suspend fun updateFollowingFeedPrefs(
        account: LoggedAccount,
        prefs: FollowingFeedPrefs,
    ): Result<Unit>? {
        if (account !is BlueskyLoggedAccount) return null
        return updatePreferences(account.locator) { current ->
            val updatedHome = PreferencesUnion.FeedViewPref(
                value = current
                    .filterIsInstance<PreferencesUnion.FeedViewPref>()
                    .firstOrNull { it.value.feed == HOME_FEED_ID }
                    ?.value
                    ?.copy(
                        hideReplies = prefs.hideReplies,
                        hideRepliesByUnfollowed = prefs.hideRepliesByUnfollowed,
                        hideReposts = prefs.hideReposts,
                        hideQuotePosts = prefs.hideQuotePosts,
                    )
                    ?: FeedViewPref(
                        feed = HOME_FEED_ID,
                        hideReplies = prefs.hideReplies,
                        hideRepliesByUnfollowed = prefs.hideRepliesByUnfollowed,
                        hideRepliesByLikeCount = null,
                        hideReposts = prefs.hideReposts,
                        hideQuotePosts = prefs.hideQuotePosts,
                    ),
            )
            current.filter {
                !(it is PreferencesUnion.FeedViewPref && it.value.feed == HOME_FEED_ID)
            } + updatedHome
        }
    }

    private fun FeedViewPref.toFollowingFeedPrefs(): FollowingFeedPrefs {
        return FollowingFeedPrefs(
            hideReplies = hideReplies == true,
            hideRepliesByUnfollowed = hideRepliesByUnfollowed == true,
            hideReposts = hideReposts == true,
            hideQuotePosts = hideQuotePosts == true,
        )
    }
}
