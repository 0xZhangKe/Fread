package com.zhangke.fread.status.blog

import kotlinx.serialization.Serializable

@Serializable
sealed interface BlogEmbed {

    @Serializable
    data class Link(
        val url: String,
        val title: String,
        val description: String,
        val video: Boolean = false,
        val authorName: String? = null,
        val authorUrl: String? = null,
        val providerName: String? = null,
        val providerUrl: String? = null,
        val html: String? = null,
        val width: Int? = null,
        val height: Int? = null,
        val image: String? = null,
        val embedUrl: String? = null,
        val blurhash: String? = null,
    ) : BlogEmbed {

        companion object {

            private val standardRange = 0.5f..2f
            private const val DEFAULT_ASPECT_RATIO = 2F
        }

        val aspectRatio: Float
            get() {
                if (width == null || height == null) return DEFAULT_ASPECT_RATIO
                val ratio = width / height.toFloat()
                if (ratio.isNaN() || ratio.isInfinite()) {
                    return DEFAULT_ASPECT_RATIO
                }
                if (ratio in standardRange) {
                    return ratio
                }
                return DEFAULT_ASPECT_RATIO
            }

    }

    @Serializable
    data class Blog(
        val blog: com.zhangke.fread.status.blog.Blog,
    ) : BlogEmbed

    @Serializable
    data class UnavailableQuote(
        val reason: String,
        val blogId: String?,
    ) : BlogEmbed
}
