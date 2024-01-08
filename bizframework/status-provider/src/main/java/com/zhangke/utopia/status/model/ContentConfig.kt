package com.zhangke.utopia.status.model

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.serialization.Serializable

@Serializable
sealed interface ContentConfig {

    @Serializable
    data class MixedContent(
        val id: Long,
        val name: String,
        val sourceUriList: List<FormalUri>,
        val lastReadStatusId: String?,
    ) : ContentConfig

    @Serializable
    data class ActivityPubContent(
        val id: Long,
        val name: String,
        val baseUrl: FormalBaseUrl,
    ) : ContentConfig

    val configName: String
        get() = when (this) {
            is MixedContent -> name
            is ActivityPubContent -> name
        }
}

