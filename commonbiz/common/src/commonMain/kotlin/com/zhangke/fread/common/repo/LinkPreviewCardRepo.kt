package com.zhangke.fread.common.repo

import com.zhangke.framework.architect.http.sharedHttpClient
import com.zhangke.framework.utils.LinkPreviewInfo
import com.zhangke.framework.utils.LinkPreviewUtils
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.takeFrom

class LinkPreviewCardRepo {

    private val urlToInfoMap = mutableMapOf<String, LinkPreviewInfo>()

    suspend fun fetchPreviewInfo(url: String): Result<LinkPreviewInfo> {
        urlToInfoMap[url]?.let { return Result.success(it) }
        val html = sharedHttpClient.get { url { takeFrom(url) } }.body<String>()
        val info = LinkPreviewUtils.fetchPreviewInfo(html) ?: return Result.failure(
            IllegalStateException("Failed to fetch link preview info")
        )
        urlToInfoMap[url] = info
        return Result.success(info)
    }
}
