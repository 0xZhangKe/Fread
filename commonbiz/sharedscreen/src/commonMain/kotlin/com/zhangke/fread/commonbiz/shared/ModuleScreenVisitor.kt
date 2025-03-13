package com.zhangke.fread.commonbiz.shared

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import cafe.adriel.voyager.core.screen.Screen

class ModuleScreenVisitor(
    val feedsScreenVisitor: IFeedsScreenVisitor,
)

interface IFeedsScreenVisitor {

    fun getAddContentScreen(): Screen
}

val LocalModuleScreenVisitor: ProvidableCompositionLocal<ModuleScreenVisitor> =
    compositionLocalOf { error("ModuleScreenVisitor not init!") }
