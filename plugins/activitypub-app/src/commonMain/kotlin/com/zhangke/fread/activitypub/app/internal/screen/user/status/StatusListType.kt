package com.zhangke.fread.activitypub.app.internal.screen.user.status

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
enum class StatusListType: PlatformSerializable {

    FAVOURITES,
    BOOKMARKS,
}
