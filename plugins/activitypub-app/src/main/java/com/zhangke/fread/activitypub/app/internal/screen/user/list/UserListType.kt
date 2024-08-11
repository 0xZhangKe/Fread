package com.zhangke.fread.activitypub.app.internal.screen.user.list

import kotlinx.serialization.Serializable

@Serializable
enum class UserListType: java.io.Serializable {

    FAVOURITES,
    REBLOGS,
    BLOCKED,
    MUTED,
    FOLLOWERS,
    FOLLOWING,

}
