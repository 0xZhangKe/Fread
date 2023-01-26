package com.zhangke.utopia.activitypubapp.utils

import android.net.Uri
import com.zhangke.framework.utils.RegexFactory

private const val HTTP_SCHEME_PREFIX = "http://"
private const val HTTPS_SCHEME_PREFIX = "https://"

internal class ActivityPubApplicableUrl(private val url: String) {

    val host: String? = RegexFactory.getDomainRegex().find(url)?.value

    val query: String?
        get() = if (validate()) {
            Uri.parse(getCompletenessUrl()).query
        } else {
            null
        }

    val path: String?
        get() = if (validate()) {
            Uri.parse(getCompletenessUrl()).path
        } else {
            null
        }

    private var completenessUrl: String? = null

    fun validate(): Boolean = !host.isNullOrEmpty()

    fun getCompletenessUrl(): String {
        if (!validate()) throw IllegalStateException("This url($url) is not validate!")
        var completenessUrl = completenessUrl
        if (completenessUrl.isNullOrEmpty()) {
            completenessUrl =
                if (url.startsWith(HTTP_SCHEME_PREFIX) || url.startsWith(HTTPS_SCHEME_PREFIX)) {
                    url
                } else {
                    "$HTTPS_SCHEME_PREFIX$url"
                }
            this.completenessUrl = completenessUrl
        }
        return completenessUrl
    }
}
}