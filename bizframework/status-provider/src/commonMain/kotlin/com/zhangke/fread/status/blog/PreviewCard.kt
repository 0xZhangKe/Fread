package com.zhangke.fread.status.blog

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
data class PreviewCard(
    val type: CardType,
    val url: String,
    val title: String,
    val description: String,
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
) : PlatformSerializable {

    companion object {

        private val standardRange = 0.5f..2f
        private const val DEFAULT_ASPECT_RATIO = 1F
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

    @Serializable
    enum class CardType : PlatformSerializable {
        LINK,
        PHOTO,
        VIDEO,
        RICH,
    }
}
