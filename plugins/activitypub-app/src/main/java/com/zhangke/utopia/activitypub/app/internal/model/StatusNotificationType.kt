package com.zhangke.utopia.activitypub.app.internal.model

enum class StatusNotificationType {

    MENTION,
    STATUS,
    REBLOG,
    FOLLOW,
    FOLLOW_REQUEST,
    FAVOURITE,
    /**
     * A poll you have voted in or created has ended
     */
    POLL,

    /**
     * A status you boosted with has been edited
     */
    UPDATE,
    UNKNOWN;
}
