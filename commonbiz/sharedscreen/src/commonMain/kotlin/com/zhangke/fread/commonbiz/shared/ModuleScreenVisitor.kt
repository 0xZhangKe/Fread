package com.zhangke.fread.commonbiz.shared

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavKey

class ModuleScreenVisitor(
    val feedsScreenVisitor: IFeedsScreenVisitor,
    val profileScreenVisitor: IProfileScreenVisitor,
)

interface IFeedsScreenVisitor {

    fun getAddContentScreen(): NavKey
}

interface IProfileScreenVisitor {

    fun getDonateScreen(): NavKey
}

val LocalModuleScreenVisitor: ProvidableCompositionLocal<ModuleScreenVisitor> =
    compositionLocalOf { error("ModuleScreenVisitor not init!") }
