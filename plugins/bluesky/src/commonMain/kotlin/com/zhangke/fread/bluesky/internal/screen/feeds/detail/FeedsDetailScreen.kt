package com.zhangke.fread.bluesky.internal.screen.feeds.detail

import androidx.compose.runtime.Composable
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.IdentityRole

class FeedsDetailScreen(
    private val feedsJson: String,
    private val role: IdentityRole,
) : BaseScreen() {

    companion object {

        fun create(feeds: BlueskyFeeds, role: IdentityRole): FeedsDetailScreen {
            return FeedsDetailScreen(
                role = role,
                feedsJson = globalJson.encodeToString(
                    serializer = BlueskyFeeds.serializer(),
                    value = feeds,
                ),
            )
        }
    }


    @Composable
    override fun Content() {
        super.Content()
    }
}
