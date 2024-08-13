package com.zhangke.fread.status.blog

import kotlinx.serialization.Serializable

@Serializable
data class BlogTranslation(
    val content: String,
    val spoilerText: String,
    val poll: Poll?,
    val attachment: Attachment,
    val detectedSourceLanguage: String,
    val provider: String,
) : java.io.Serializable {

    @Serializable
    data class Poll(
        val id: String,
        val options: List<Option>,
    ) : java.io.Serializable {

        @Serializable
        data class Option(
            val title: String,
        )
    }

    @Serializable
    data class Attachment(
        val id: String,
        val description: String,
    ) : java.io.Serializable
}
