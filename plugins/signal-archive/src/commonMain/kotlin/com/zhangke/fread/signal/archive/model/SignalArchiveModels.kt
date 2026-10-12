package com.zhangke.fread.signal.archive.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Reading index from rendered.json. Timestamps are ISO 8601 UTC strings. */
@Serializable
data class SignalArchiveIndex(
    val children: List<SignalArchiveNode>,
    val startedAt: String? = null,
    val finishedAt: String? = null,
    val durationSeconds: Int? = null,
)

/** Array order defines navigation and pagination order; paths are relative to the data URL. */
@Serializable
data class SignalArchiveNode(
    val type: SignalArchiveNodeType,
    val title: String,
    val articleCount: Int,
    val pages: List<String>,
    val children: List<SignalArchiveNode> = emptyList(),
    @SerialName("failed_count")
    val failedCount: Int? = null,
    val state: Double? = null,
    val lastContentChangedAt: String? = null,
    val lastSuccessfulFetchAt: String? = null,
    val originalUrl: String? = null,
)

@Serializable
enum class SignalArchiveNodeType {
    @SerialName("category")
    CATEGORY,
    @SerialName("rss")
    RSS,
    @SerialName("ai")
    AI,
}

@Serializable
data class SignalArchivePage(
    val items: List<SignalArchiveArticleSummary>,
)

@Serializable
data class SignalArchiveArticleSummary(
    val id: String,
    val title: String,
    val summary: String,
    val sourceId: String,
    val sourceTitle: String,
    val detailPath: String,
    val image: String? = null,
    val publishedAt: String? = null,
    val link: String? = null,
)

@Serializable
data class SignalArchiveArticle(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val sourceId: String,
    val sourceTitle: String,
    val image: String? = null,
    val publishedAt: String? = null,
    val link: String? = null,
)
