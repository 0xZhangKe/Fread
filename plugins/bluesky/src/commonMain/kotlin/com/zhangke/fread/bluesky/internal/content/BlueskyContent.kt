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
import com.zhangke.framework.architect.json.JsonModuleBuilder
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.security.Md5
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.commonbiz.bluesky_logo
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.uri.FormalUri
import com.zhangke.krouter.annotation.Service
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import org.jetbrains.compose.resources.painterResource

@Service
class BlueskyContentJsonModuleBuilder : JsonModuleBuilder {

    override fun SerializersModuleBuilder.buildSerializersModule() {
        polymorphic(
            baseClass = FreadContent::class,
            actualClass = BlueskyContent::class,
            actualSerializer = serializer(),
        )
    }
}

@Serializable
data class BlueskyContent(
    override val name: String,
    override val order: Int,
    val baseUrl: FormalBaseUrl,
    val feedsList: List<BlueskyFeeds>,
    override val accountUri: FormalUri? = null,
) : FreadContent {

    private val _id: String by lazy {
        if (accountUri == null) {
            Md5.md5(baseUrl.toString())
        } else {
            Md5.md5(baseUrl.toString() + accountUri.toString())
        }
    }

    override val id: String get() = _id

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
