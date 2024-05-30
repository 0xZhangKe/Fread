package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.utopia.activitypub.app.internal.composable.ActivityPubTabNames
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubTimelineType
import com.zhangke.utopia.status.model.IdentityRole

class ActivityPubTimelineTab(
    private val role: IdentityRole,
    private val type: ActivityPubTimelineType,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = when (type) {
                ActivityPubTimelineType.HOME -> ActivityPubTabNames.homeTimeline
                ActivityPubTimelineType.LOCAL -> ActivityPubTabNames.localTimeline
                ActivityPubTimelineType.PUBLIC -> ActivityPubTabNames.publicTimeline
            }
        )

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        TODO("Not yet implemented")
    }
}