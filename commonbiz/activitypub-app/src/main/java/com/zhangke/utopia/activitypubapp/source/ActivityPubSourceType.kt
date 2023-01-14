package com.zhangke.utopia.activitypubapp.source

internal enum class ActivityPubSourceType(val stringValue: String) {

    LOCAL_TIMELINE("LocalTimeline"),
    PUBLIC_TIMELINE("PublicTimeline"),
    USER("User");
}