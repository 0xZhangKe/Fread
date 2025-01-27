package com.zhangke.fread.bluesky.internal.repo

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.createBlueskyProtocol
import com.zhangke.fread.bluesky.internal.uri.platform.PlatformUriTransformer
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class BlueskyPlatformRepo @Inject constructor(
    private val platformUriTransformer: PlatformUriTransformer,
) {

    suspend fun getAllPlatform(): List<BlogPlatform> {
        val baseUrl = FormalBaseUrl.parse("https://bsky.social")!!
        return listOf(createBlueskyPlatform(baseUrl))
    }

    suspend fun getPlatform(baseUrl: FormalBaseUrl): BlogPlatform {
        return createBlueskyPlatform(baseUrl)
    }

    private fun createBlueskyPlatform(baseUrl: FormalBaseUrl): BlogPlatform {
        return BlogPlatform(
            uri = platformUriTransformer.build(baseUrl).toString(),
            name = "Bluesky",
            description = "Bluesky is social media as it should be. Find your community among millions of users, unleash your creativity, and have some fun again.",
            thumbnail = "https://fread.xyz/resources/bsky_logo.svg",
            protocol = createBlueskyProtocol(),
            baseUrl = baseUrl,
        )
    }
}
