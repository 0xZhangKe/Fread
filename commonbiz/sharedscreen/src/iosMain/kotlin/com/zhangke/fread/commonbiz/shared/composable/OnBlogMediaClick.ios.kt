package com.zhangke.fread.commonbiz.shared.composable

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.status.ui.image.BlogMediaClickEvent

actual fun onStatusMediaClick(
    transparentNavigator: TransparentNavigator,
    navigator: NavBackStack<NavKey>,
    event: BlogMediaClickEvent,
) {
    // TODO: Not implemented yet
}
