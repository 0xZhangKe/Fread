package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ActivityPubTimelineContainerViewModel (
    private val statusProvider: StatusProvider,
    buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
    private val timelineRepo: ActivityPubTimelineStatusRepo,
)