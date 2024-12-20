package com.zhangke.fread.common.action

object RouteActions {

    const val ACTION_KEY = "route_action"

    const val BASE_URI = "fread://fread.xyz/action"

}

object OpenNotificationPageAction {

    const val URI = "${RouteActions.BASE_URI}/open_notification_page"

    fun buildOpenNotificationPageRoute(): String {
        return buildString {
            append(URI)
        }
    }
}
