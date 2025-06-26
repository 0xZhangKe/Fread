package com.zhangke.fread.activitypub.app.internal.screen.user.about

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.date.DateParser
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.common.utils.formatDefault
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class UserAboutViewModel(
    private val clientManager: ActivityPubClientManager,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    val locator: PlatformLocator,
    val webFinger: WebFinger,
    val userId: String?,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(
        UserAboutUiState(
            joinedDatetime = null,
            fieldList = emptyList(),
            emojis = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _messageFlow = MutableSharedFlow<TextString>()
    val messageFlow = _messageFlow.asSharedFlow()

    init {
        launchInViewModel {
            val userId = if (this@UserAboutViewModel.userId.isNullOrEmpty()) {
                val accountIdResult = webFingerBaseUrlToUserIdRepo.getUserId(webFinger, locator)
                if (accountIdResult.isFailure) {
                    accountIdResult.exceptionOrNull()?.message?.let {
                        _messageFlow.emit(textOf(it))
                    }
                    return@launchInViewModel
                }
                accountIdResult.getOrThrow()
            } else {
                this@UserAboutViewModel.userId
            }
            clientManager.getClient(locator)
                .accountRepo
                .getAccount(userId)
                .onFailure { e ->
                    e.message?.let { _messageFlow.emit(textOf(it)) }
                }.onSuccess { entity ->
                    _uiState.value = _uiState.value.copy(
                        joinedDatetime = DateParser.parseOrCurrent(entity.createdAt)
                            .formatDefault(),
                        fieldList = entity.fields,
                        emojis = entity.emojis.map(emojiEntityAdapter::toEmoji),
                    )
                }
        }
    }
}
