package com.zhangke.fread.commonbiz.shared.composable

import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.status.ui.image.BlogMediaClickEvent

expect fun onStatusMediaClick(
    transparentNavigator: TransparentNavigator,
    navigator: Navigator,
    event: BlogMediaClickEvent,
)
