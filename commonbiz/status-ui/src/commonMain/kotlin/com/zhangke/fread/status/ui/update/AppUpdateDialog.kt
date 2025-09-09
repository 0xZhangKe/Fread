package com.zhangke.fread.status.ui.update

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.fread.common.update.AppReleaseInfo
import com.zhangke.fread.localization.LocalizedString
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
        title = stringResource(LocalizedString.statusUiUpdateDialogTitle),
        contentText = stringResource(
            LocalizedString.statusUiUpdateDialogReleaseNote,
            appReleaseInfo.versionName,
            appReleaseInfo.releaseNote,
        ),
        onNegativeClick = { onCancel(appReleaseInfo) },
        onPositiveClick = { onUpdateClick(appReleaseInfo) },
    )
}
