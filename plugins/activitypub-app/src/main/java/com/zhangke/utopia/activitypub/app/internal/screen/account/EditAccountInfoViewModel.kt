package com.zhangke.utopia.activitypub.app.internal.screen.account

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.status.uri.FormalUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class EditAccountInfoViewModel @AssistedInject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo,
    private val clientManager: ActivityPubClientManager,
    @Assisted private val accountUri: FormalUri,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(uri: FormalUri): EditAccountInfoViewModel
    }

    private val _uiState = MutableStateFlow(
        EditAccountUiState(
            name = "",
            editName = "",
            banner = "",
            editBanner = "",
            avatar = "",
            editAvatar = "",
            description = "",
            editDescription = ""
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow: SharedFlow<TextString> = _snackBarMessageFlow.asSharedFlow()

    init {
        launchInViewModel {
            accountRepo.queryByUri(accountUri.toString())

        }
    }
}