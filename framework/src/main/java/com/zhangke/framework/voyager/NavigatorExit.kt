package com.zhangke.framework.voyager

import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.krouter.KRouter

fun Navigator.pushDestination(routerDestination: String): Boolean {
    val destination = KRouter.routeScreen(routerDestination) ?: return false
    push(destination)
    return true
}

fun Navigator.tryPush(screen: Any): Boolean {
    val realScreen = screen as? Screen ?: return false
    push(realScreen)
    return true
}

fun TransparentNavigator.pushDestination(routerDestination: String): Boolean {
    val destination = KRouter.routeScreen(routerDestination) ?: return false
    push(destination)
    return true
}

private fun KRouter.routeScreen(destination: String): Screen? {
    route<Screen>(destination)?.let { return it }
    route<AndroidScreen>(destination)?.let { return it }
    return null
}

val Navigator.rootNavigator: Navigator
    get() {
        if (parent != null) return parent!!.rootNavigator
        return this
    }
