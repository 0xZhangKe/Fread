package com.zhangke.fread.common.resources

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.bluesky_logo
import com.zhangke.fread.commonbiz.mastodon_logo
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.isActivityPub
import com.zhangke.fread.status.model.isBluesky
import com.zhangke.fread.status.model.isRss
import org.jetbrains.compose.resources.vectorResource

val StatusProviderProtocol.logo: ImageVector
    @Composable get() {
        return when {
            isActivityPub -> mastodonLogo()
            isRss -> rssLogo()
            isBluesky -> blueskyLogo()
            else -> rssLogo()
        }
    }

@Composable
fun PlatformLogo(
    modifier: Modifier,
    protocol: StatusProviderProtocol,
) {
    Box(
        modifier = modifier,
    ) {
        val logo = when {
            protocol.isBluesky -> blueskyLogo()
            protocol.isActivityPub -> mastodonLogo()
            else -> null
        }
        if (logo != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                imageVector = logo,
                contentDescription = null,
            )
        }
    }
}

@Composable
fun mastodonLogo(): ImageVector {
    return vectorResource(Res.drawable.mastodon_logo)
}

@Composable
fun blueskyLogo(): ImageVector {
    return vectorResource(Res.drawable.bluesky_logo)
}

@Composable
fun rssLogo(): ImageVector {
    return Icons.Default.RssFeed
}
