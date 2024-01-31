package com.zhangke.utopia.activitypub.app.internal.screen.user.about

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = UserAboutViewModel.Factory::class)
class UserAboutViewModel @AssistedInject constructor(
    @Assisted userUriInsights: UserUriInsights,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(userUriInsights: UserUriInsights): UserAboutViewModel
    }

    private val _uiState = MutableStateFlow(
        UserAboutUiState(
            "",
        )
    )
    val uiState = _uiState.asStateFlow()
}
