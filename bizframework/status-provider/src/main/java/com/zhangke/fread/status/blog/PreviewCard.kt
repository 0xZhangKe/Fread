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

    val aspectRatio: Float
        get() = width / height.toFloat()

    @Serializable
    enum class CardType : java.io.Serializable {
        LINK,
        PHOTO,
        VIDEO,
        RICH,
    }
}
