package com.zhangke.utopia.activitypubapp.utils

import android.net.Uri
import android.util.Base64
import com.zhangke.framework.utils.RegexFactory

private const val HTTP_SCHEME_PREFIX = "http://"
private const val HTTPS_SCHEME_PREFIX = "https://"

internal class ActivityPubUrl private constructor(
    val host: String,
    val path: String?,
    val query: String?,
    val completenessUrl: String,
) {

    fun encodeToBase64(): String {
        return completenessUrl.encodeToBase64()
    }

    override fun toString(): String {
        return completenessUrl
    }

    companion object {

        fun create(url: String): ActivityPubUrl? {
            val host = RegexFactory.getDomainRegex().find(url)?.value
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