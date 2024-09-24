package com.zhangke.fread.activitypub.app.internal.screen.user.status

import kotlinx.serialization.Serializable

@Serializable
enum class StatusListType: java.io.Serializable {

    FAVOURITES,
    BOOKMARKS,
}
