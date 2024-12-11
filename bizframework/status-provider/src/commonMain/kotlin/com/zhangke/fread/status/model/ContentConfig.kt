package com.zhangke.fread.status.model

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable

@Serializable
sealed interface ContentConfig {

    val id: Long
    val order: Int

    val configName: String
        get() = when (this) {
            is MixedContent -> name
            is ActivityPubContent -> name
            is BlueskyContent -> name
        }

    @Serializable
    data class MixedContent(
        override val id: Long,
        override val order: Int,
        val name: String,
        val sourceUriList: List<FormalUri>,
    ) : ContentConfig

    @Serializable
    data class ActivityPubContent(
        override val id: Long,
        override val order: Int,
        val name: String,
        val baseUrl: FormalBaseUrl,
        val showingTabList: List<ContentTab>,
        val hiddenTabList: List<ContentTab>,
    ) : ContentConfig {

        // hidden 列表中的 order 字段可能没有意义，因为并不会按照它在首页排序
        @Serializable
        sealed class ContentTab {

            abstract val order: Int

            abstract fun newOrder(order: Int): ContentTab

            @Serializable
            data class HomeTimeline(
                override val order: Int,
            ) : ContentTab() {
                override fun newOrder(order: Int): ContentTab {
                    return copy(order = order)
                }
            }

            @Serializable
            data class LocalTimeline(
                override val order: Int,
            ) : ContentTab() {

                override fun newOrder(order: Int): ContentTab {
                    return copy(order = order)
                }
            }

            @Serializable
            data class PublicTimeline(
                override val order: Int,
            ) : ContentTab() {

                override fun newOrder(order: Int): ContentTab {
                    return copy(order = order)
                }
            }

            @Serializable
            data class Trending(
                override val order: Int,
            ) : ContentTab() {

                override fun newOrder(order: Int): ContentTab {
                    return copy(order = order)
                }
            }

            @Serializable
            data class ListTimeline(
                val listId: String,
                val name: String,
                override val order: Int,
            ) : ContentTab() {

                override fun newOrder(order: Int): ContentTab {
                    return copy(order = order)
                }
            }
        }
    }

    @Serializable
    data class BlueskyContent(
        override val id: Long,
        override val order: Int,
        val name: String,
        val baseUrl: FormalBaseUrl,
    ) : ContentConfig {

    }
}

fun List<ContentConfig.ActivityPubContent.ContentTab>.dropNotExistListTab(
    allListId: Set<String>
): List<ContentConfig.ActivityPubContent.ContentTab> {
    return this.filter {
        if (it is ContentConfig.ActivityPubContent.ContentTab.ListTimeline) {
            it.listId in allListId
        } else {
            true
        }
    }
}
