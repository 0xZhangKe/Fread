package com.zhangke.utopia.activitypubapp.protocol

import com.zhangke.utopia.status.source.StatusProviderUri

private const val path = "/status/owner/"

fun activityPubOwnerUri(domain: String): StatusProviderUri {
    return buildActivityPubSourceUri(path, mapOf("domain" to domain))
}
