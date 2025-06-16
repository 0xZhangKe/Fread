package com.zhangke.fread.activitypub.app.internal.content

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
import com.zhangke.framework.security.Md5
import com.zhangke.fread.commonbiz.mastodon_logo
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource

@Serializable
data class ActivityPubContent(
    override val name: String,
    override val order: Int,
    val baseUrl: FormalBaseUrl,
    val tabList: List<ContentTab>,
    val accountUri: FormalUri? = null,
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
                painter = painterResource(com.zhangke.fread.commonbiz.Res.drawable.mastodon_logo),
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

    @Serializable
    sealed interface ContentTab {

        val order: Int

        val hide: Boolean

        fun newOrder(order: Int): ContentTab

        fun updateHide(hide: Boolean): ContentTab

        @Serializable
        data class HomeTimeline(
            override val order: Int,
            override val hide: Boolean = false,
        ) : ContentTab {

            override fun updateHide(hide: Boolean): ContentTab {
                return copy(hide = hide)
            }

            override fun newOrder(order: Int): ContentTab {
                return copy(order = order)
            }
        }

        @Serializable
        data class LocalTimeline(
            override val order: Int,
            override val hide: Boolean = false,
        ) : ContentTab {

            override fun updateHide(hide: Boolean): ContentTab {
                return copy(hide = hide)
            }

            override fun newOrder(order: Int): ContentTab {
                return copy(order = order)
            }
        }

        @Serializable
        data class PublicTimeline(
            override val order: Int,
            override val hide: Boolean = false,
        ) : ContentTab {

            override fun updateHide(hide: Boolean): ContentTab {
                return copy(hide = hide)
            }

            override fun newOrder(order: Int): ContentTab {
                return copy(order = order)
            }
        }

        @Serializable
        data class Trending(
            override val order: Int,
            override val hide: Boolean = false,
        ) : ContentTab {

            override fun updateHide(hide: Boolean): ContentTab {
                return copy(hide = hide)
            }

            override fun newOrder(order: Int): ContentTab {
                return copy(order = order)
            }
        }

        @Serializable
        data class ListTimeline(
            val listId: String,
            val name: String,
            override val order: Int,
            override val hide: Boolean = false,
        ) : ContentTab {

            override fun updateHide(hide: Boolean): ContentTab {
                return copy(hide = hide)
            }

            override fun newOrder(order: Int): ContentTab {
                return copy(order = order)
            }
        }
    }
}
