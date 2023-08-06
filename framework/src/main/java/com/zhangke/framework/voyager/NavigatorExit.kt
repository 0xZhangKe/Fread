package com.zhangke.framework.voyager

import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.krouter.KRouter

fun Navigator.pushDestination(routerDestination: String): Boolean {
    val destination = KRouter.route<AndroidScreen>(routerDestination) ?: return false
    push(destination)
    return true
}
