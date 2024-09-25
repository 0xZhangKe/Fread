package com.zhangke.fread.activitypub.app.internal.screen.user.list

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
enum class UserListType: PlatformSerializable {

    FAVOURITES,
    REBLOGS,
    BLOCKED,
    MUTED,
    FOLLOWERS,
    FOLLOWING,

}
