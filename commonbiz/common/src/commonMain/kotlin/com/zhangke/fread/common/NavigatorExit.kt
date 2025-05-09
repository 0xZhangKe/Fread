package com.zhangke.fread.common

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.krouter.KRouter

fun Navigator.tryPush(screen: Any): Boolean {
    if (screen is String) {
        return pushDestination(screen)
    }
    val realScreen = screen as? Screen ?: return false
    push(realScreen)
    return true
}

fun Navigator.pushDestination(routerDestination: String): Boolean {
    val destination = KRouter.routeScreen(routerDestination) ?: return false
    push(destination)
    return true
}

fun TransparentNavigator.pushDestination(routerDestination: String): Boolean {
    val destination = KRouter.routeScreen(routerDestination) ?: return false
    push(destination)
    return true
}


fun KRouter.routeScreen(destination: String): Screen? {
    route<Screen>(destination)?.let { return it }
    route<BaseScreen>(destination)?.let { return it }
    return null
}
