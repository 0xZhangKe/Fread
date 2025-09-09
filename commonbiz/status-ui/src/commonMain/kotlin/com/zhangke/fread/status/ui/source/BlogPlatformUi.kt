package com.zhangke.fread.status.ui.source

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.fread.common.resources.logo
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.img_banner_background
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlogPlatformCard(
    modifier: Modifier,
    platform: BlogPlatform,
    onLoginClick: (() -> Unit)?,
) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .aspectRatio(2F)
                ) {
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(Res.drawable.img_banner_background),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                    AutoSizeImage(
                        url = platform.thumbnail.orEmpty(),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        contentDescription = "banner",
                    )
                }
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 6.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = platform.name,
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Image(
                        modifier = Modifier.size(12.dp),
                        imageVector = platform.protocol.logo,
                        contentDescription = null,
                    )
                }
                Text(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 2.dp,
                    ),
                    text = platform.baseUrl.host,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
                FreadRichText(
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            top = 2.dp,
                            end = 16.dp,
                        ),
                    content = platform.description,
                    maxLines = 3,
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (onLoginClick != null) {
                    Button(
                        modifier = Modifier.padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        onClick = onLoginClick,
                    ) {
                        Text(
                            text = stringResource(LocalizedString.login),
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun BlogPlatformUi(
    modifier: Modifier,
    platform: BlogPlatform,
    showDivider: Boolean = true,
) {
    SourceCommonUi(
        modifier = modifier,
        thumbnail = platform.thumbnail.orEmpty(),
        title = platform.name,
        subtitle = platform.baseUrl.host,
        description = platform.description,
        protocolLogo = platform.protocol.logo,
        showDivider = showDivider,
    )
}

@Composable
fun BlogPlatformSnapshotUi(
    modifier: Modifier,
    platform: PlatformSnapshot,
) {
    SourceCommonUi(
        modifier = modifier,
        thumbnail = platform.thumbnail,
        title = platform.domain,
        subtitle = platform.domain,
        description = platform.description,
        protocolLogo = platform.protocol.logo,
    )
}
