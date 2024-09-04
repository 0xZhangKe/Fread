package com.zhangke.fread.common

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

fun Navigator.tryPush(screen: Any): Boolean {
    if (screen is String) {
        return pushDestination(screen)
    }
    val realScreen = screen as? Screen ?: return false
    push(realScreen)
    return true
}

expect fun Navigator.pushDestination(routerDestination: String): Boolean
