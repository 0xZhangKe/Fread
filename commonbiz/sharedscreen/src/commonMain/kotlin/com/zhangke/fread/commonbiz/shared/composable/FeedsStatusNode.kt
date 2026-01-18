package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.commonbiz.shared.screen.status.account.SelectAccountOpenStatusBottomSheet
import com.zhangke.fread.commonbiz.shared.screen.status.account.rememberSelectAccountOpenStatusSheetState
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusUi
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun FeedsStatusNode(
    modifier: Modifier = Modifier,
    status: StatusUiState,
    indexInList: Int,
    composedStatusInteraction: ComposedStatusInteraction,
    showDivider: Boolean = true,
    style: StatusStyle = LocalStatusUiConfig.current.contentStyle,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    val selectAccountOpenStatusBottomSheetState = rememberSelectAccountOpenStatusSheetState()
    StatusUi(
        modifier = modifier.clickable {
            composedStatusInteraction.onStatusClick(status)
        },
        status = status,
        indexInList = indexInList,
        style = style,
        showDivider = showDivider,
        composedStatusInteraction = composedStatusInteraction,
        onMediaClick = { event ->
            onStatusMediaClick(
                navigator = backStack,
                event = event,
            )
        },
        onOpenBlogWithOtherAccountClick = {
            selectAccountOpenStatusBottomSheetState.show(it)
        },
    )
    SelectAccountOpenStatusBottomSheet(selectAccountOpenStatusBottomSheetState)
}
