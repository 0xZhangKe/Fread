package com.zhangke.fread.status.ui.style

import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalStatusStyle: ProvidableCompositionLocal<StatusStyle?> =
    compositionLocalOf { null }

object StatusStyles {

    @Composable
    fun small(): StatusStyle {
        return StatusStyle(
            containerStartPadding = 16.dp,
            containerTopPadding = 10.dp,
            containerEndPadding = 16.dp,
            containerBottomPadding = 10.dp,
            topLabelStyle = smallTopLabelStyle(),
            infoLineStyle = smallInfoStyle(),
            contentStyle = smallContentStyle(),
            bottomPanelStyle = smallBottomPanelStyle(),
            threadsStyle = smallThreadsStyle(),
            cardStyle = smallCardStyle(),
        )
    }

    @Composable
    fun medium(): StatusStyle {
        return StatusStyle(
            containerStartPadding = 16.dp,
            containerTopPadding = 12.dp,
            containerEndPadding = 16.dp,
            containerBottomPadding = 12.dp,
            topLabelStyle = mediumTopLabelStyle(),
            infoLineStyle = mediumInfoStyle(),
            contentStyle = mediumContentStyle(),
            bottomPanelStyle = mediumBottomPanelStyle(),
            threadsStyle = mediumThreadsStyle(),
            cardStyle = mediumCardStyle(),
        )
    }

    @Composable
    fun large(): StatusStyle {
        return StatusStyle(
            containerStartPadding = 16.dp,
            containerTopPadding = 14.dp,
            containerEndPadding = 16.dp,
            containerBottomPadding = 14.dp,
            topLabelStyle = largeTopLabelStyle(),
            infoLineStyle = largeInfoStyle(),
            contentStyle = largeContentStyle(),
            bottomPanelStyle = largeBottomPanelStyle(),
            threadsStyle = largeThreadsStyle(),
            cardStyle = largeCardStyle(),
        )
    }

    @Composable
    private fun smallThreadsStyle(): StatusStyle.ThreadsStyle {
        return mediumThreadsStyle()
    }

    @Composable
    private fun mediumThreadsStyle(): StatusStyle.ThreadsStyle {
        return StatusStyle.ThreadsStyle(
            lineWidth = 1.5.dp,
            color = DividerDefaults.color,
        )
    }

    @Composable
    private fun largeThreadsStyle(): StatusStyle.ThreadsStyle {
        return mediumThreadsStyle()
    }

    @Composable
    private fun smallTopLabelStyle(): StatusStyle.TopLabelStyle {
        return StatusStyle.TopLabelStyle(
            iconSize = 12.dp,
            textSize = 11.sp,
        )
    }

    @Composable
    private fun mediumTopLabelStyle(): StatusStyle.TopLabelStyle {
        return StatusStyle.TopLabelStyle(
            iconSize = 14.dp,
            textSize = 12.sp,
        )
    }

    @Composable
    private fun largeTopLabelStyle(): StatusStyle.TopLabelStyle {
        return StatusStyle.TopLabelStyle(
            iconSize = 16.dp,
            textSize = 14.sp,
        )
    }

    @Composable
    private fun smallInfoStyle(): StatusStyle.InfoLineStyle {
        return StatusStyle.InfoLineStyle(
            nameSize = 14.sp,
            avatarSize = 36.dp,
            nameToAvatarSpacing = 6.dp,
            descStyle = MaterialTheme.typography.bodySmall
        )
    }

    @Composable
    private fun mediumInfoStyle(): StatusStyle.InfoLineStyle {
        return StatusStyle.InfoLineStyle(
            nameSize = 16.sp,
            avatarSize = 40.dp,
            nameToAvatarSpacing = 8.dp,
            descStyle = MaterialTheme.typography.bodySmall
        )
    }

    @Composable
    private fun largeInfoStyle(): StatusStyle.InfoLineStyle {
        return StatusStyle.InfoLineStyle(
            nameSize = 18.sp,
            avatarSize = 44.dp,
            nameToAvatarSpacing = 8.dp,
            descStyle = MaterialTheme.typography.bodyMedium
        )
    }

    @Composable
    private fun smallContentStyle(): StatusStyle.ContentStyle {
        return StatusStyle.ContentStyle(
            maxLine = 10,
            titleSize = 14.sp,
            contentSize = 12.sp,
            startPadding = 0.dp,
            contentToInfoLineSpacing = 4.dp,
            textToAttachmentSpacing = 6.dp
        )
    }

    @Composable
    private fun mediumContentStyle(): StatusStyle.ContentStyle {
        return StatusStyle.ContentStyle(
            maxLine = 10,
            titleSize = 16.sp,
            contentSize = 14.sp,
            startPadding = 0.dp,
            contentToInfoLineSpacing = 6.dp,
            textToAttachmentSpacing = 8.dp
        )
    }

    @Composable
    private fun largeContentStyle(): StatusStyle.ContentStyle {
        return StatusStyle.ContentStyle(
            maxLine = 10,
            titleSize = 18.sp,
            contentSize = 16.sp,
            startPadding = 0.dp,
            contentToInfoLineSpacing = 8.dp,
            textToAttachmentSpacing = 10.dp
        )
    }

    @Composable
    private fun smallBottomPanelStyle(): StatusStyle.BottomPanelStyle {
        return StatusStyle.BottomPanelStyle(
            iconSize = 28.dp,
            topPadding = 2.dp,
            startPadding = 0.dp
        )
    }

    @Composable
    private fun mediumBottomPanelStyle(): StatusStyle.BottomPanelStyle {
        return StatusStyle.BottomPanelStyle(
            iconSize = 32.dp,
            topPadding = 4.dp,
            startPadding = 0.dp
        )
    }

    @Composable
    private fun largeBottomPanelStyle(): StatusStyle.BottomPanelStyle {
        return StatusStyle.BottomPanelStyle(
            iconSize = 34.dp,
            topPadding = 6.dp,
            startPadding = 0.dp
        )
    }

    @Composable
    private fun smallCardStyle(): StatusStyle.CardStyle {
        return StatusStyle.CardStyle(
            titleStyle = MaterialTheme.typography.titleSmall,
            descStyle = MaterialTheme.typography.bodySmall,
            imageBottomPadding = 6.dp,
            contentVerticalPadding = 6.dp
        )
    }

    @Composable
    private fun mediumCardStyle(): StatusStyle.CardStyle {
        return StatusStyle.CardStyle(
            titleStyle = MaterialTheme.typography.titleMedium,
            descStyle = MaterialTheme.typography.bodyMedium,
            imageBottomPadding = 8.dp,
            contentVerticalPadding = 8.dp
        )
    }

    @Composable
    private fun largeCardStyle(): StatusStyle.CardStyle {
        return StatusStyle.CardStyle(
            titleStyle = MaterialTheme.typography.titleLarge,
            descStyle = MaterialTheme.typography.bodyLarge,
            imageBottomPadding = 10.dp,
            contentVerticalPadding = 10.dp
        )
    }
}
