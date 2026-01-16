package com.zhangke.fread.commonbiz.shared.composable

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.status.ui.image.BlogMediaClickEvent

expect fun onStatusMediaClick(
    transparentNavigator: TransparentNavigator,
    navigator: NavBackStack<NavKey>,
    event: BlogMediaClickEvent,
)
