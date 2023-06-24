package com.zhangke.utopia.activitypubapp.uri

import com.zhangke.utopia.status.utils.StatusProviderUri

open class ActivityPubUri protected constructor(
    private val path: String,
    private val queries: Map<String, String>
) {

    override fun toString(): String {
        val query = buildQuery()
        return "${StatusProviderUri.SCHEME}://$HOST$path$query"
    }

    private fun buildQuery(): String {
        return queries.entries.joinToString(prefix = "?", separator = "&") {
            "${it.key}=${it.value}"
        }
    }

    companion object {

        const val HOST = "activitypub.com"
    }
}

