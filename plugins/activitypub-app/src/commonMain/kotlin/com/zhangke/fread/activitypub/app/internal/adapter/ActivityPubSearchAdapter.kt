package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubSearchEntity
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.search.SearchResult
import me.tatarka.inject.annotations.Inject

class ActivityPubSearchAdapter @Inject constructor(
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val hashtagAdapter: ActivityPubTagAdapter,
    private val statusAdapter: ActivityPubStatusAdapter,
) {

    suspend fun toSearchResult(
        entity: ActivityPubSearchEntity,
        platform: BlogPlatform,
        locator: PlatformLocator,
        account: ActivityPubLoggedAccount?,
    ): List<SearchResult> {
        val authorList = entity.accounts.map {
            SearchResult.Author(accountEntityAdapter.toAuthor(it))
        }
        val hashtagList = entity.hashtags.map {
            SearchResult.SearchedHashtag(hashtagAdapter.adapt(it))
        }
        val statusList = entity.statuses.map {
            SearchResult.SearchedStatus(statusAdapter.toStatusUiState(it, platform, locator, account))
        }
        return authorList + hashtagList + statusList
    }
}
