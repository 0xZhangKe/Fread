package com.zhangke.utopia.status.model

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.serialization.Serializable

@Serializable
sealed interface ContentConfig {

    val id: Long
    val order: Int

    val configName: String
        get() = when (this) {
            is MixedContent -> name
            is ActivityPubContent -> name
        }

    @Serializable
    data class MixedContent(
        override val id: Long,
        override val order: Int,
        val name: String,
        val sourceUriList: List<FormalUri>,
        val lastReadStatusId: String?,
    ) : ContentConfig

    @Serializable
    data class ActivityPubContent(
        override val  id: Long,
        override val order: Int,
        val name: String,
        val baseUrl: FormalBaseUrl,
    ) : ContentConfig
}
