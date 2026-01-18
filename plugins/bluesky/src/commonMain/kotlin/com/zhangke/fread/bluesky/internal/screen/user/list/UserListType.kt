package com.zhangke.fread.bluesky.internal.screen.user.list

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
enum class UserListType : PlatformSerializable {

    LIKE,
    REBLOG,
    FOLLOWING,
    FOLLOWERS,
    BLOCKED,
    MUTED,

}
