package com.zhangke.fread.commonbiz.shared

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import cafe.adriel.voyager.core.screen.Screen

class ModuleScreenVisitor(
    val feedsScreenVisitor: IFeedsScreenVisitor,
    val profileScreenVisitor: IProfileScreenVisitor,
)

interface IFeedsScreenVisitor {

    fun getAddContentScreen(): Screen
}

interface IProfileScreenVisitor {

    fun getDonateScreen(): Screen
}

val LocalModuleScreenVisitor: ProvidableCompositionLocal<ModuleScreenVisitor> =
    compositionLocalOf { error("ModuleScreenVisitor not init!") }
