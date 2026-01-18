package com.zhangke.fread.commonbiz.shared.composable

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.zhangke.fread.status.ui.image.BlogMediaClickEvent

expect fun onStatusMediaClick(
    navigator: NavBackStack<NavKey>,
    event: BlogMediaClickEvent,
)
