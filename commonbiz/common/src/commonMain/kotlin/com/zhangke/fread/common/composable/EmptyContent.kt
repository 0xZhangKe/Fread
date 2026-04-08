package com.zhangke.fread.common.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.img_empty_account
import com.zhangke.fread.commonbiz.img_empty_content
import com.zhangke.fread.commonbiz.img_empty_message
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

enum class EmptyContentType {

    Content,
    Message,
    Account;

    fun getDrawableResource(): DrawableResource {
        return when (this) {
            Content -> Res.drawable.img_empty_content
            Message -> Res.drawable.img_empty_message
            Account -> Res.drawable.img_empty_account
        }
    }
}

@Composable
fun EmptyContent(
    modifier: Modifier,
    type: EmptyContentType = EmptyContentType.Content,
    contentTitle: String = stringResource(LocalizedString.emptyContentHintTitle),
    subtitle: String? = stringResource(LocalizedString.emptyContentHintDesc),
    onClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentScale = ContentScale.Inside,
                painter = painterResource(type.getDrawableResource()),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                text = contentTitle,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.padding(start = 32.dp, top = 8.dp, end = 32.dp),
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (onClick != null) {
            Button(
                modifier = Modifier.padding(top = 32.dp),
                onClick = onClick,
            ) {
                Text(text = stringResource(LocalizedString.feedsAddContent))
            }
        }
    }
}
