package com.zhangke.utopia.activitypub.app.internal.screen.user.follow

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.db.WebFingerBaseurlToIdEntity
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.uri.FormalUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class FollowViewModel @AssistedInject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val userUriTransformer: UserUriTransformer,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    @Assisted private val userUri: FormalUri,
    @Assisted private val isFollowing: Boolean,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(userUri: FormalUri, isFollowing: Boolean): FollowViewModel
    }

    private val _uiState = MutableStateFlow(
        FollowUiState(
            refreshing = false,
            loadMoreState = LoadState.Idle,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _messageFlow = MutableSharedFlow<TextString>()
    val messageFlow = _messageFlow.asSharedFlow()

    init {
        launchInViewModel {
            val userInsight = userUriTransformer.parse(userUri)
            if (userInsight == null) {
                _messageFlow.emit(textOf("Invalid user uri: $userUri"))
                return@launchInViewModel
            }

        }
    }

    fun onRefresh() {

    }

    fun onLoadMore() {

    }

}
