package com.zhangke.fread.bluesky.internal.client

import sh.christian.ozone.api.Nsid

object BskyCollections {

    val feedLike = Nsid("app.bsky.feed.like")

    val feedRepost = Nsid("app.bsky.feed.repost")

    val feedPost = Nsid("app.bsky.feed.post")

    val profile = Nsid("app.bsky.actor.profile")

    val follow = Nsid("app.bsky.graph.follow")

    val block = Nsid("app.bsky.graph.block")

    val postGate = Nsid("app.bsky.feed.postgate")

    val threadGate = Nsid("app.bsky.feed.threadgate")
}
