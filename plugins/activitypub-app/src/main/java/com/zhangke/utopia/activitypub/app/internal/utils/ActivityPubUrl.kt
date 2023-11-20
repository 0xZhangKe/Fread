package com.zhangke.utopia.activitypub.app.internal.utils

import android.net.Uri
import android.util.Base64
import com.zhangke.framework.utils.RegexFactory
import java.net.URL

private const val HTTP_SCHEME_PREFIX = "http://"
private const val HTTPS_SCHEME_PREFIX = "https://"

internal class ActivityPubUrl private constructor(
    val host: String,
    val path: String?,
    val query: String?,
    val completenessUrl: String,
) {

    override fun toString(): String {
        return completenessUrl
    }

    companion object {

        fun create(url: String): ActivityPubUrl? {
            val host = RegexFactory.getDomainRegex().find(url.toDomain())?.value
            if (host.isNullOrEmpty()) return null
            val completenessUrl = buildCompletenessUrl(url)
            val uri = Uri.parse(completenessUrl)
            return ActivityPubUrl(
                host = host,
                path = uri.path,
                query = uri.query,
                completenessUrl = completenessUrl
            )
        }

        private fun buildCompletenessUrl(url: String): String {
            return if (url.startsWith(HTTP_SCHEME_PREFIX) || url.startsWith(HTTPS_SCHEME_PREFIX)) {
                url
            } else {
                "$HTTPS_SCHEME_PREFIX$url"
            }
        }

    }
}