package com.zhangke.fread.bluesky.internal.repo

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.uri.platform.PlatformUriTransformer
import com.zhangke.fread.status.model.createBlueskyProtocol
import com.zhangke.fread.status.platform.BlogPlatform

class BlueskyPlatformRepo(
    private val platformUriTransformer: PlatformUriTransformer,
) {

    private val appToBackendDomainMap = mapOf(
        "bsky.app" to "bsky.social"
    )

    val appViewDomains: Set<String> = appToBackendDomainMap.keys.toSet()

    fun getAllPlatform(): List<BlogPlatform> {
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
            thumbnail = "https://web-cdn.bsky.app/static/apple-touch-icon.png",
            protocol = createBlueskyProtocol(),
            baseUrl = baseUrl,
            supportsQuotePost = true,
        )
    }

    fun mapAppToBackendDomain(domain: String): String {
        return appToBackendDomainMap[domain]?.takeIf { it.isNotEmpty() } ?: domain
    }
}
