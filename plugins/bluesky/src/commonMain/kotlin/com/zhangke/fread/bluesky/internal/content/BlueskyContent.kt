package com.zhangke.fread.bluesky.internal.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.commonbiz.bluesky_logo
import com.zhangke.fread.status.model.FreadContent
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource

@Serializable
data class BlueskyContent(
    override val id: String,
    override val name: String,
    override val order: Int,
    val baseUrl: FormalBaseUrl,
    val feedsList: List<BlueskyFeeds>,
) : FreadContent {

    override fun newOrder(newOrder: Int): FreadContent {
        return copy(order = newOrder)
    }

    @Composable
    override fun Subtitle() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier.size(14.dp),
                painter = painterResource(com.zhangke.fread.commonbiz.Res.drawable.bluesky_logo),
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .align(Alignment.Bottom),
                text = baseUrl.host,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
