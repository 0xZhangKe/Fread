package com.zhangke.fread.status.search

/**
 * Sort order for status search results.
 *
 * Currently only honored by Bluesky's `app.bsky.feed.searchPosts` endpoint —
 * other engines (ActivityPub, RSS) ignore the value and behave as if
 * [LATEST] were requested.
 */
enum class SearchStatusSort {
    LATEST,
    TOP,
}
