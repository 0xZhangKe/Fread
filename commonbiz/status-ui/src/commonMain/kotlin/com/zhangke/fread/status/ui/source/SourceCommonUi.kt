package com.zhangke.fread.status.ui.source

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.rememberImageActionPainter
import com.seiko.imageloader.ui.AutoSizeBox
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.browser.launchWebTabInApp
import com.zhangke.fread.status.ui.richtext.FreadRichText

@Composable
fun SourceCommonUi(
    thumbnail: String,
    title: String,
    subtitle: String?,
    description: String,
    protocolLogo: ImageVector?,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = modifier) {
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(Modifier.width(16.dp))
            Box(
                modifier = Modifier.size(48.dp),
            ) {
                AutoSizeBox(
                    url = thumbnail,
                    modifier = Modifier.fillMaxSize(),
                ) { action ->
                    Image(
                        modifier = Modifier
                            .freadPlaceholder(
                                visible = action !is ImageAction.Success,
                                shape = CircleShape,
                            )
                            .matchParentSize()
                            .clip(CircleShape),
                        painter = rememberImageActionPainter(action),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                    if (protocolLogo != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Image(
                            modifier = Modifier.size(12.dp),
                            imageVector = protocolLogo,
                            contentDescription = null,
                        )
                    }
                }
                if (subtitle.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(0.5.dp))
                } else {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                Spacer(Modifier.height(2.dp))
                FreadRichText(
                    content = description,
                    maxLines = 3,
                    emojis = emptyList(),
                    mentions = emptyList(),
                    tags = emptyList(),
                    onMentionClick = {},
                    onHashtagClick = {},
                    onUrlClick = {
                        browserLauncher.launchWebTabInApp(coroutineScope, it)
                    },
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        if (showDivider) {
            HorizontalDivider()
        }
    }
}
