package com.zhangke.fread.activitypub.app.internal.composable

import androidx.compose.runtime.Composable
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_content_tab_home
import com.zhangke.fread.activitypub.app.activity_pub_content_tab_local_timeline
import com.zhangke.fread.activitypub.app.activity_pub_content_tab_public_timeline
import com.zhangke.fread.activitypub.app.activity_pub_content_tab_trending
import com.zhangke.fread.status.model.ContentConfig.ActivityPubContent.ContentTab
import org.jetbrains.compose.resources.stringResource

internal object ActivityPubTabNames {

    val homeTimeline: String
        @Composable get() = stringResource(Res.string.activity_pub_content_tab_home)

    val publicTimeline: String
        @Composable get() = stringResource(Res.string.activity_pub_content_tab_public_timeline)

    val localTimeline: String
        @Composable get() = stringResource(Res.string.activity_pub_content_tab_local_timeline)

    val trending: String
        @Composable get() = stringResource(Res.string.activity_pub_content_tab_trending)
}

@Composable
internal fun ContentTab.tabName(): String {
    return when (this) {
        is ContentTab.HomeTimeline -> ActivityPubTabNames.homeTimeline
        is ContentTab.PublicTimeline -> ActivityPubTabNames.publicTimeline
        is ContentTab.LocalTimeline -> ActivityPubTabNames.localTimeline
        is ContentTab.Trending -> ActivityPubTabNames.trending
        is ContentTab.ListTimeline -> this.name
    }
}
