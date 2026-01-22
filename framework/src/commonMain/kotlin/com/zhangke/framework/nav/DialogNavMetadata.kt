package com.zhangke.framework.nav

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.scene.DialogSceneStrategy

const val FREAD_DIALOG_METADATA_KEY = "fread.dialog"

fun dialogMetadata(
    properties: DialogProperties = DialogProperties(),
): Map<String, Any> {
    return DialogSceneStrategy.dialog(properties) + mapOf(FREAD_DIALOG_METADATA_KEY to true)
}
