package com.zhangke.fread.activitypub.app.internal.screen.hashtag

import android.annotation.SuppressLint
import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

@SuppressLint("StaticFieldLeak")
class HashtagTimelineContainerViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val context: ApplicationContext,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : ContainerViewModel<HashtagTimelineViewModel, HashtagTimelineContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): HashtagTimelineViewModel {
        return HashtagTimelineViewModel(
            clientManager = clientManager,
            statusProvider = statusProvider,
            context = context,
            statusUpdater = statusUpdater,
            statusAdapter = statusAdapter,
            platformRepo = platformRepo,
            buildStatusUiState = buildStatusUiState,
            refactorToNewBlog = refactorToNewBlog,
            role = params.role,
            hashtag = params.tag,
        )
    }

    fun getViewModel(role: IdentityRole, tag: String): HashtagTimelineViewModel {
        val params = Params(role, tag)
        return obtainSubViewModel(params)
    }

    class Params(
        val role: IdentityRole,
        val tag: String,
    ) : SubViewModelParams() {

        override val key: String
            get() = role.toString() + tag
    }
}