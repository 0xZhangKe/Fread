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
import androidx.compose.ui.graphics.vector.ImageVector
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.StatusActionType
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
    blog: Blog,
    isOwner: Boolean,
    blogTranslationState: BlogTranslationUiState,
    style: StatusStyle,
    onActionClick: (StatusActionType, Blog) -> Unit,
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
                blogUrl = blog.url,
                blogTranslationState = blogTranslationState,
                onDismissRequest = { showMorePopup = false },
                onTranslateClick = onTranslateClick,
            )

            if (isOwner) {
                InteractionItem(
                    type = StatusActionType.PIN,
                    icon = pinIcon(blog.pinned),
                    actionName = pinAlt(blog.pinned),
                    onDismissRequest = { showMorePopup = false },
                    onActionClick = { onActionClick(it, blog) },
                )

                InteractionItem(
                    type = StatusActionType.DELETE,
                    icon = deleteIcon(),
                    actionName = deleteAlt(),
                    onDismissRequest = { showMorePopup = false },
                    onActionClick = { onActionClick(it, blog) },
                )
                if (blog.supportEdit) {
                    InteractionItem(
                        type = StatusActionType.EDIT,
                        icon = editIcon(),
                        actionName = editAlt(),
                        onDismissRequest = { showMorePopup = false },
                        onActionClick = { onActionClick(it, blog) },
                    )
                }
            }
        }
    }
}

@Composable
private fun InteractionItem(
    type: StatusActionType,
    actionName: String,
    icon: ImageVector,
    onDismissRequest: () -> Unit,
    onActionClick: (StatusActionType) -> Unit,
) {
    var showDeleteConfirmDialog by remember(type) {
        mutableStateOf(false)
    }
    DropdownMenuItem(
        text = { Text(text = actionName) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = actionName,
            )
        },
        onClick = {
            reportStatusInteractionClickEvent(type)
            if (type == StatusActionType.DELETE) {
                showDeleteConfirmDialog = true
            } else {
                onDismissRequest()
                onActionClick(type)
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
                onActionClick(type)
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
    val textHandler = LocalActivityTextHandler.current
    val browserLauncher = LocalActivityBrowserLauncher.current
    DropDownOpenInBrowserItem {
        reportClick(StatusDataElements.OPEN_IN_BROWSER)
        onDismissRequest()
        browserLauncher.launchWebTabInApp(blogUrl, checkAppSupportPage = false)
    }
    DropDownCopyLinkItem {
        reportClick(StatusDataElements.COPY_BLOG_LINK)
        onDismissRequest()
        textHandler.copyText(blogUrl)
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
