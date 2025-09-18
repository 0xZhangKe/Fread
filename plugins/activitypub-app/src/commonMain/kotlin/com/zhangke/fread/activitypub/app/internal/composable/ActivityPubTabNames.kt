package com.zhangke.fread.activitypub.app.internal.composable

import androidx.compose.runtime.Composable
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

internal object ActivityPubTabNames {

    val homeTimeline: String
        @Composable get() = stringResource(LocalizedString.activity_pub_content_tab_home)

    val publicTimeline: String
        @Composable get() = stringResource(LocalizedString.activity_pub_content_tab_public_timeline)

    val localTimeline: String
        @Composable get() = stringResource(LocalizedString.activity_pub_content_tab_local_timeline)

    val trending: String
        @Composable get() = stringResource(LocalizedString.activity_pub_content_tab_trending)
}

@Composable
internal fun ActivityPubContent.ContentTab.tabName(): String {
    return when (this) {
        is ActivityPubContent.ContentTab.HomeTimeline -> ActivityPubTabNames.homeTimeline
        is ActivityPubContent.ContentTab.PublicTimeline -> ActivityPubTabNames.publicTimeline
        is ActivityPubContent.ContentTab.LocalTimeline -> ActivityPubTabNames.localTimeline
        is ActivityPubContent.ContentTab.Trending -> ActivityPubTabNames.trending
        is ActivityPubContent.ContentTab.ListTimeline -> this.name
    }
}
