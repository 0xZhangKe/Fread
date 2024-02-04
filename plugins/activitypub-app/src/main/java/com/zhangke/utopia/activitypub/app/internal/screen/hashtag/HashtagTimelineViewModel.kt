package com.zhangke.utopia.activitypub.app.internal.screen.hashtag

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel(assistedFactory = HashtagTimelineViewModel.Factory::class)
class HashtagTimelineViewModel @Inject constructor(
    private val baseUrlManager: BaseUrlManager,
    private val clientManager: ActivityPubClientManager,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    @Assisted private val hashtag: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(hashtag: String): HashtagTimelineViewModel
    }

    init {

    }

    fun onRefresh(){
        refresh()
    }

    private fun refresh(){

    }
}
