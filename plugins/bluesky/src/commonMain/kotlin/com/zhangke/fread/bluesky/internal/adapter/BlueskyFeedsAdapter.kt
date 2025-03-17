package com.zhangke.fread.bluesky.internal.adapter

import app.bsky.actor.SavedFeed
import app.bsky.feed.GeneratorView
import app.bsky.graph.ListView
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import me.tatarka.inject.annotations.Inject

class BlueskyFeedsAdapter @Inject constructor(
    private val profileAdapter: BlueskyProfileAdapter,
) {

    fun convertToFeeds(
        savedFeed: SavedFeed,
        generator: GeneratorView,
    ): BlueskyFeeds.Feeds {
        return BlueskyFeeds.Feeds(
            uri = generator.uri.atUri,
            cid = generator.cid.cid,
            did = generator.did.did,
            pinned = savedFeed.pinned,
            following = true,
            displayName = generator.displayName,
            description = generator.description,
            avatar = generator.avatar?.uri,
            likeCount = generator.likeCount,
            likedRecord = generator.viewer?.like?.atUri,
            creator = profileAdapter.convertToProfile(generator.creator),
        )
    }

    fun convertToFeeds(
        generator: GeneratorView,
        following: Boolean,
        pinned: Boolean,
    ): BlueskyFeeds.Feeds {
        return BlueskyFeeds.Feeds(
            uri = generator.uri.atUri,
            cid = generator.cid.cid,
            did = generator.did.did,
            pinned = pinned,
            following = following,
            displayName = generator.displayName,
            description = generator.description,
            avatar = generator.avatar?.uri,
            likeCount = generator.likeCount,
            likedRecord = generator.viewer?.like?.atUri,
            creator = profileAdapter.convertToProfile(generator.creator),
        )
    }

    fun convertToList(
        feed: SavedFeed,
        listView: ListView,
        following: Boolean,
    ): BlueskyFeeds.List {
        return BlueskyFeeds.List(
            id = feed.id,
            uri = feed.value,
            name = listView.name,
            description = listView.description,
            avatar = listView.avatar?.uri,
            following = following,
            pinned = feed.pinned,
        )
    }
}
