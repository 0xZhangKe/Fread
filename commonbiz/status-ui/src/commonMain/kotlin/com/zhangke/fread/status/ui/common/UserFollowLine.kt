package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.utils.formatToHumanReadable
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_user_detail_follower_info
import com.zhangke.fread.statusui.status_ui_user_detail_following_info
import com.zhangke.fread.statusui.status_ui_user_detail_posts
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun UserFollowLine(
    modifier: Modifier,
    followersCount: Long?,
    followingCount: Long?,
    statusesCount: Long?,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CountInfoItem(
            count = followersCount,
            descId = Res.string.status_ui_user_detail_follower_info,
            onClick = onFollowerClick,
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 4.dp),
            text = "·",
            style = MaterialTheme.typography.bodySmall,
        )
        CountInfoItem(
            count = followingCount,
            descId = Res.string.status_ui_user_detail_following_info,
            onClick = onFollowingClick,
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 4.dp),
            text = "·",
            style = MaterialTheme.typography.bodySmall,
        )
        CountInfoItem(
            count = statusesCount,
            descId = Res.string.status_ui_user_detail_posts,
        )
    }
}

@Composable
private fun CountInfoItem(
    count: Long?,
    descId: StringResource,
    onClick: (() -> Unit)? = null,
) {
    val descSuffix = stringResource(descId)
    val info = remember(count) {
        if (count == null) {
            buildAnnotatedString { append("    ") }
        } else {
            buildCountedDesc(count, descSuffix)
        }
    }
    Text(
        modifier = Modifier.clickable(count != null && onClick != null) {
            onClick?.invoke()
        },
        text = info,
        style = MaterialTheme.typography.bodySmall,
    )
}

private fun buildCountedDesc(count: Long, desc: String): AnnotatedString {
    val formattedCount = count.formatToHumanReadable()
    return buildAnnotatedString {
        append(formattedCount)
        addStyle(
            style = SpanStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
            start = 0,
            end = formattedCount.length,
        )
        append(" ")
        append(desc)
    }
}
