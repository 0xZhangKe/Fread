package com.zhangke.fread.status.content

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class MixedContent(
    override val id: String,
    override val order: Int,
    override val name: String,
    val sourceUriList: List<FormalUri>,
) : FreadContent {

    override val accountUri: FormalUri? = null

    override fun newOrder(newOrder: Int): FreadContent {
        return copy(order = newOrder)
    }

    @Composable
    override fun Subtitle(account: LoggedAccount?) {
        Text(
            modifier = Modifier,
            text = buildSubtitle(),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }

    @Composable
    private fun buildSubtitle(): AnnotatedString {
        return buildAnnotatedString {
            val prefix = stringResource(LocalizedString.mixed_content_subtitle_1)
            val suffix = stringResource(LocalizedString.mixed_content_subtitle_2)
            append(prefix)
            val sizeString = " " + sourceUriList.size.toString() + " "
            append(sizeString)
            addStyle(
                style = SpanStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                ),
                start = prefix.length,
                end = prefix.length + sizeString.length,
            )
            append(suffix)
        }
    }
}
