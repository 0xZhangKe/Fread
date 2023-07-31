package com.zhangke.utopia.activitypubapp.uri

import com.zhangke.utopia.status.uri.StatusProviderUri
import com.zhangke.utopia.status.uri.statusProviderUriString

open class ActivityPubUri protected constructor(
    private val path: String,
    private val queries: Map<String, String>
) {

    override fun toString(): String {
        return activityPubUriString(path, queries)
    }

    fun toStatusProviderUri(): StatusProviderUri {
        return StatusProviderUri.build(
            host = HOST,
            path = path,
            queries = queries,
        )
    }

    companion object {

        const val HOST = "activitypub.com"
    }
}

fun activityPubUriString(path: String, queries: Map<String, String>): String {
    return statusProviderUriString(ActivityPubUri.HOST, path, queries)
}
