package com.zhangke.utopia.activitypubapp.utils

internal object WebFingerUtil {

    fun findWebFinger(content: String): WebFinger? {
        return findWebFingerAsWebFinger(content) ?: findWebFingerAsUrl(content)
    }

    private fun findWebFingerAsWebFinger(content: String): WebFinger? {
        val webFinger = WebFinger(content.removePrefix("acct:"))
        if (webFinger.validate()) return webFinger
        return null
    }

    private fun findWebFingerAsUrl(content: String): WebFinger? {
        val url = ActivityPubUrl(content)
        if (!url.validate()) return null
        if (url.path.isNullOrEmpty()) return null
        val pathToWebFinger = WebFinger(url.path!!)
        if (pathToWebFinger.validate()) return pathToWebFinger
        val hostAndPathWebFinger = WebFinger("${url.path}@${url.toughHost}")
        if (hostAndPathWebFinger.validate()) return hostAndPathWebFinger
        return null
    }
}