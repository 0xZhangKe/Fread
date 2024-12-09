package com.zhangke.fread.bluesky.internal.uri.user

import com.zhangke.fread.bluesky.internal.uri.BlueskyUriPath
import com.zhangke.fread.bluesky.internal.uri.createBlueskyUri
import com.zhangke.fread.bluesky.internal.uri.isBlueskyUri
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class UserUriTransformer @Inject constructor() {

    companion object {

        private const val QUERY_DID = "did"
    }

    fun createUserUri(did: String): FormalUri {
        val queries = mapOf(QUERY_DID to did)
        return createBlueskyUri(
            path = BlueskyUriPath.USER,
            queries = queries,
        )
    }

    fun parse(uri: FormalUri): UserUriInsights? {
        if (!uri.isBlueskyUri) return null
        if (uri.path != BlueskyUriPath.USER) return null
        val did = uri.queries[QUERY_DID]
        if (did.isNullOrEmpty()) return null
        return UserUriInsights(uri, did)
    }
}
