package com.zhangke.framework.voyager

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.Stack
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.CoroutineScope

class TransparentNavigator internal constructor(
    private val navigator: Navigator,
): Stack<Screen> by navigator{

}
