package com.zhangke.utopia.activitypubapp.utils

import com.zhangke.framework.utils.RegexFactory

private const val HTTP_SCHEME_PREFIX = "http://"
private const val HTTPS_SCHEME_PREFIX = "https://"

internal class ActivityPubApplicableUrl(private val url: String) {

    val host: String? = RegexFactory.getDomainRegex().find(url)?.value

    fun validate(): Boolean = !host.isNullOrEmpty()

    fun getCompletenessUrl(): String {
        if (!validate()) throw IllegalStateException("This url($url) is not validate!")
        return if (url.startsWith(HTTP_SCHEME_PREFIX) || url.startsWith(HTTPS_SCHEME_PREFIX)) {
            url
        } else {
            "$HTTPS_SCHEME_PREFIX$url"
        }
    }
}