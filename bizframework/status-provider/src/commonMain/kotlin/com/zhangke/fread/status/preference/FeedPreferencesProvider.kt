package com.zhangke.fread.status.preference

import com.zhangke.fread.status.account.LoggedAccount

data class FollowingFeedPrefs(
    val hideReplies: Boolean = false,
    val hideRepliesByUnfollowed: Boolean = false,
    val hideReposts: Boolean = false,
    val hideQuotePosts: Boolean = false,
)

interface IFeedPreferencesProvider {

    /**
     * Returns the current Following-feed preferences for the given account.
     * Returns null when the platform does not expose server-side feed prefs.
     */
    suspend fun getFollowingFeedPrefs(account: LoggedAccount): Result<FollowingFeedPrefs>? {
        return null
    }

    /**
     * Persists [prefs] for [account]. Returns null when the platform does not
     * support server-side feed prefs.
     */
    suspend fun updateFollowingFeedPrefs(
        account: LoggedAccount,
        prefs: FollowingFeedPrefs,
    ): Result<Unit>? {
        return null
    }
}

class FeedPreferencesProvider(
    private val providers: List<IFeedPreferencesProvider>,
) {

    suspend fun getFollowingFeedPrefs(account: LoggedAccount): Result<FollowingFeedPrefs>? {
        return providers.firstNotNullOfOrNull { it.getFollowingFeedPrefs(account) }
    }

    suspend fun updateFollowingFeedPrefs(
        account: LoggedAccount,
        prefs: FollowingFeedPrefs,
    ): Result<Unit> {
        return providers.firstNotNullOfOrNull { it.updateFollowingFeedPrefs(account, prefs) }
            ?: Result.success(Unit)
    }
}
