package com.zhangke.fread.status.ui.action

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.utils.SystemUtils
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.status.ui.StatusDataElements
import com.zhangke.fread.status.ui.reportStatusInteractionClickEvent
import com.zhangke.fread.statusui.R

@Composable
fun StatusMoreInteractionIcon(
    modifier: Modifier,
    blogUrl: String,
    moreActionList: List<StatusUiInteraction>,
    onActionClick: (StatusUiInteraction) -> Unit,
) {
    var showMorePopup by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier) {
        SimpleIconButton(
            modifier = Modifier,
            onClick = {
                reportClick(StatusDataElements.MORE)
                showMorePopup = !showMorePopup
            },
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More",
        )

        DropdownMenu(
            expanded = showMorePopup,
            onDismissRequest = { showMorePopup = false },
        ) {
            AdditionalMoreOptions(
                blogUrl = blogUrl,
                onDismissRequest = { showMorePopup = false },
            )
            moreActionList.forEach { interaction ->
                InteractionItem(
                    interaction = interaction,
                    onDismissRequest = { showMorePopup = false },
                    onActionClick = onActionClick,
                )
            }
        }
    }
}

@Composable
private fun InteractionItem(
    interaction: StatusUiInteraction,
    onDismissRequest: () -> Unit,
    onActionClick: (StatusUiInteraction) -> Unit,
) {
    var showDeleteConfirmDialog by remember(interaction) {
        mutableStateOf(false)
    }
    DropdownMenuItem(
        text = {
            Text(text = interaction.actionName)
        },
        leadingIcon = {
            Icon(
                imageVector = interaction.logo,
                contentDescription = interaction.actionName,
            )
        },
        onClick = {
            reportStatusInteractionClickEvent(interaction)
            if (interaction is StatusUiInteraction.Delete) {
                showDeleteConfirmDialog = true
            } else {
                onDismissRequest()
                onActionClick(interaction)
            }
        },
    )
    if (showDeleteConfirmDialog) {
        FreadDialog(
            onDismissRequest = {
                onDismissRequest()
                showDeleteConfirmDialog = false
            },
            contentText = stringResource(R.string.status_ui_delete_status_confirm),
            onNegativeClick = {
                onDismissRequest()
                showDeleteConfirmDialog = false
            },
            onPositiveClick = {
                onDismissRequest()
                showDeleteConfirmDialog = false
                onActionClick(interaction)
            },
        )
    }
}

@Composable
private fun AdditionalMoreOptions(
    blogUrl: String,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    DropDownOpenInBrowserItem {
        reportClick(StatusDataElements.OPEN_IN_BROWSER)
        onDismissRequest()
        BrowserLauncher.launchWebTabInApp(context, blogUrl, checkAppSupportPage = false)
    }
    DropDownCopyLinkItem {
        reportClick(StatusDataElements.COPY_BLOG_LINK)
        onDismissRequest()
        SystemUtils.copyText(context, blogUrl)
    }
}
