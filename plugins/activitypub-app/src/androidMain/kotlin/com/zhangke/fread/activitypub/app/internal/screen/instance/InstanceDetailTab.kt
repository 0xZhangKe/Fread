package com.zhangke.fread.activitypub.app.internal.screen.instance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.screen.instance.about.ServerAboutPage
import com.zhangke.fread.activitypub.app.internal.screen.instance.tags.ServerTrendsTagsPage

internal enum class InstanceDetailTab(
    val title: TextString,
    val content: @Composable Screen.(
        baseUrl: FormalBaseUrl,
        rules: List<ActivityPubInstanceEntity.Rule>,
        contentCanScrollBackward: MutableState<Boolean>,
    ) -> Unit,
) {

    ABOUT(
        title = textOf(R.string.activity_pub_about),
        content = @Composable { baseUrl, rules, contentCanScrollBackward ->
            ServerAboutPage(baseUrl, rules, contentCanScrollBackward)
        },
    ),

    TRENDS_TAG(
        title = textOf(R.string.activity_pub_trends_tag),
        content = @Composable { baseUrl, _, contentCanScrollBackward ->
            ServerTrendsTagsPage(baseUrl, contentCanScrollBackward)
        },
    ),
}
