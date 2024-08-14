package com.zhangke.fread.status.blog

import kotlinx.serialization.Serializable

@Serializable
data class PreviewCard(
    val type: CardType,
    val url: String,
    val title: String,
    val description: String,
    val authorName: String,
    val authorUrl: String,
    val providerName: String,
    val providerUrl: String,
    val html: String,
    val width: Int,
    val height: Int,
    val image: String?,
    val embedUrl: String,
    val blurhash: String?,
) : java.io.Serializable {

    companion object {

        private val standardRange = 0.5f..2f
        private const val DEFAULT_ASPECT_RATIO = 1F
    }

    val aspectRatio: Float
        get() {
            val ratio = width / height.toFloat()
            if (ratio.isNaN() || ratio.isInfinite()) {
                return DEFAULT_ASPECT_RATIO
            }
            if (ratio in standardRange) {
                return ratio
            }
            return DEFAULT_ASPECT_RATIO
        }

    @Serializable
    enum class CardType : java.io.Serializable {
        LINK,
        PHOTO,
        VIDEO,
        RICH,
    }
}
