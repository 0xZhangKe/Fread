package com.zhangke.utopia.activitypubapp.source

internal enum class ActivityPubSourceType(val stringValue: String) {

    LOCAL_TIMELINE("LocalTimeline"),
    PUBLIC_TIMELINE("PublicTimeline"),
    HOME_TIMELINE("HomeTimeline"),
    USER_STATUS("UserStatus"),
    USER_STATUS_EXCLUDE_REPLIES("UserStatusExcludeReplies");
}