package com.zhangke.framework.voyager

import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator

const val ROOT_NAVIGATOR_KEY = "com.zhangke.framework.voyager.ROOT_NAVIGATOR"

@OptIn(InternalVoyagerApi::class)
val Navigator.rootNavigator: Navigator
    get() {
        var rootNavigator = this
        while (rootNavigator.key != ROOT_NAVIGATOR_KEY) {
            rootNavigator = rootNavigator.parent ?: break
        }
        return rootNavigator
    }
