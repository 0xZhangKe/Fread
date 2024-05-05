package com.zhangke.utopia.activitypub.app.internal.screen.user.about

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DateFormat
import java.util.Locale

class UserAboutViewModel(
    private val clientManager: ActivityPubClientManager,
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val emojiEntityAdapter: ActivityPubCustomEmojiEntityAdapter,
    val role: IdentityRole,
    val webFinger: WebFinger,
) : SubViewModel() {

    private val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())

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
            val accountIdResult =
                webFingerBaseUrlToUserIdRepo.getUserId(webFinger, role)
            if (accountIdResult.isFailure) {
                accountIdResult.exceptionOrNull()?.message?.let {
                    _messageFlow.emit(textOf(it))
                }
                return@launchInViewModel
            }
            clientManager.getClient(role)
                .accountRepo
                .getAccount(accountIdResult.getOrThrow())
                .onFailure { e ->
                    e.message?.let { _messageFlow.emit(textOf(it)) }
                }.onSuccess { entity ->
                    _uiState.value = _uiState.value.copy(
                        joinedDatetime = dateFormat.format(formatDatetimeToDate(entity.createdAt)),
                        fieldList = entity.fields,
                        emojis = entity.emojis.map(emojiEntityAdapter::toEmoji),
                    )
                }
        }
    }
}
