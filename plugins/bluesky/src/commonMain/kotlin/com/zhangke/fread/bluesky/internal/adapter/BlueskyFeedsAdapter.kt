package com.zhangke.fread.bluesky.internal.adapter

import app.bsky.feed.GeneratorView
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import me.tatarka.inject.annotations.Inject

class BlueskyFeedsAdapter @Inject constructor(
    private val accountAdapter: BlueskyAccountAdapter,
) {

    fun convertToFeeds(generatorView: GeneratorView): BlueskyFeeds {
        return BlueskyFeeds(
            uri = generatorView.uri.atUri,
            cid = generatorView.cid.cid,
            did = generatorView.did.did,
            displayName = generatorView.displayName,
            description = generatorView.description,
            avatar = generatorView.avatar?.uri,
            likeCount = generatorView.likeCount,
            creator = accountAdapter.convertToBlogAuthor(generatorView.creator),
        )
    }
}
