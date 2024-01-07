package com.zhangke.utopia.status.model

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.serialization.Serializable

@Serializable
sealed interface ContentConfig {

    @Serializable
    data class MixedContent(
        val id: String,
        val name: String,
        val sourceUriList: List<FormalUri>,
        val lastReadStatusId: String?,
    ) : ContentConfig

    @Serializable
    data class MastodonContent(
        private val name: String,
        private val baseUrl: FormalBaseUrl,
    ) : ContentConfig
}
