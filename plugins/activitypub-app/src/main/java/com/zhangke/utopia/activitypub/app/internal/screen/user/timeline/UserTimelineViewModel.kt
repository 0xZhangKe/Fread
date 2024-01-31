package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = UserTimelineViewModel.Factory::class)
class UserTimelineViewModel @AssistedInject constructor(
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val clientManager: ActivityPubClientManager,
    private val baseUrlManager: BaseUrlManager,
    @Assisted val userUriInsights: UserUriInsights,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(userUriInsights: UserUriInsights): UserTimelineViewModel
    }

    private val _uiState = MutableStateFlow(
        UserTimelineUiState(
            status = emptyList(),
            refreshing = false,
            loading = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    private fun refresh() {
        launchInViewModel {
            getClient().accountRepo.getStatuses(

            )

        }
    }

    private suspend fun getAccountId(): Result<String> {
        return webFingerBaseUrlToUserIdRepo.getUserId(userUriInsights.webFinger, getBaseUrl())
    }

    private suspend fun getClient(): ActivityPubClient {
        return clientManager.getClient(getBaseUrl())
    }

    private suspend fun getBaseUrl(): FormalBaseUrl {
        return baseUrlManager.decideBaseUrl(userUriInsights.baseUrl)
    }
}
