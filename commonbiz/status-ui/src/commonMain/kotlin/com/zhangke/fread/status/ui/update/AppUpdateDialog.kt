package com.zhangke.fread.status.ui.update

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.fread.common.update.AppReleaseInfo
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_update_dialog_release_note
import com.zhangke.fread.statusui.status_ui_update_dialog_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppUpdateDialog(
    appReleaseInfo: AppReleaseInfo,
    onCancel: (AppReleaseInfo) -> Unit,
    onUpdateClick: (AppReleaseInfo) -> Unit,
) {
    FreadDialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
        title = stringResource(Res.string.status_ui_update_dialog_title),
        contentText = stringResource(
            Res.string.status_ui_update_dialog_release_note,
            appReleaseInfo.versionName,
            appReleaseInfo.releaseNote,
        ),
        onNegativeClick = { onCancel(appReleaseInfo) },
        onPositiveClick = { onUpdateClick(appReleaseInfo) },
    )
}
