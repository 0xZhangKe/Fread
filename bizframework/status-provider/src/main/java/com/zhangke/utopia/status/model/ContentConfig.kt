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
        override val id: Long,
        override val order: Int,
        val name: String,
        val baseUrl: FormalBaseUrl,
        val showingTabList: List<ContentTab>,
        val hideTabList: List<ContentTab>,
    ) : ContentConfig {

        @Serializable
        sealed class ContentTab {

            @Serializable
            data object HomeTimeline : ContentTab()

            @Serializable
            data object LocalTimeline : ContentTab()

            @Serializable
            data object PublicTimeline : ContentTab()

            @Serializable
            data object Trending : ContentTab()

            @Serializable
            data class ListTimeline(
                val listId: Long,
            ) : ContentTab()
        }

        @Serializable
        data class TabConfig(
            val tab: ContentTab,
            val order: Int,
        )
    }
}
