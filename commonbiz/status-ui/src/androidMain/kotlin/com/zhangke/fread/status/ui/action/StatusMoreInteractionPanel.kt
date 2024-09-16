package com.zhangke.fread.status.ui.action

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
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
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.utils.SystemUtils
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.status.ui.StatusDataElements
import com.zhangke.fread.status.ui.reportStatusInteractionClickEvent
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.ic_more
import com.zhangke.fread.statusui.status_ui_delete_status_confirm
import com.zhangke.fread.statusui.status_ui_interaction_translate
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun StatusMoreInteractionIcon(
    modifier: Modifier,
    blogUrl: String,
    blogTranslationState: BlogTranslationUiState,
    style: StatusStyle,
    moreActionList: List<StatusUiInteraction>,
    onActionClick: (StatusUiInteraction) -> Unit,
    onTranslateClick: () -> Unit,
) {
    var showMorePopup by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier) {
        StatusIconButton(
            modifier = Modifier
                .size(style.bottomPanelStyle.iconSize),
            onClick = {
                reportClick(StatusDataElements.MORE)
                showMorePopup = !showMorePopup
            },
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_more),
                contentDescription = "More Options"
            )
        }

        DropdownMenu(
            expanded = showMorePopup,
            onDismissRequest = { showMorePopup = false },
        ) {
            AdditionalMoreOptions(
                blogUrl = blogUrl,
                blogTranslationState = blogTranslationState,
                onDismissRequest = { showMorePopup = false },
                onTranslateClick = onTranslateClick,
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
            contentText = stringResource(Res.string.status_ui_delete_status_confirm),
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
    blogTranslationState: BlogTranslationUiState,
    onDismissRequest: () -> Unit,
    onTranslateClick: () -> Unit,
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
    if (blogTranslationState.support) {
        ModalDropdownMenuItem(
            text = stringResource(Res.string.status_ui_interaction_translate),
            imageVector = Icons.Default.Language,
            onClick = {
                reportClick(StatusDataElements.TRANSLATE)
                onDismissRequest()
                onTranslateClick()
            },
        )
    }
}
