package com.zhangke.fread.bluesky.internal.adapter

import app.bsky.actor.SavedFeed
import app.bsky.feed.GeneratorView
import app.bsky.graph.ListView
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds

class BlueskyFeedsAdapter(
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
        pinned: Boolean,
    ): BlueskyFeeds.Feeds {
        return BlueskyFeeds.Feeds(
            uri = generator.uri.atUri,
            cid = generator.cid.cid,
            did = generator.did.did,
            pinned = pinned,
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
    ): BlueskyFeeds.List {
        return BlueskyFeeds.List(
            id = feed.id,
            uri = feed.value,
            name = listView.name,
            description = listView.description,
            avatar = listView.avatar?.uri,
            pinned = feed.pinned,
        )
    }
}
