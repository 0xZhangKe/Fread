package com.zhangke.utopia.activitypub.app.internal.screen.user.about

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.emoji.MapAccountEntityEmojiUseCase
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
    private val mapAccountEntityEmoji: MapAccountEntityEmojiUseCase,
    val role: IdentityRole,
    val userUriInsights: UserUriInsights,
) : SubViewModel() {

    private val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())

    private val _uiState = MutableStateFlow(
        UserAboutUiState(
            joinedDatetime = null,
            fieldList = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _messageFlow = MutableSharedFlow<TextString>()
    val messageFlow = _messageFlow.asSharedFlow()

    init {
        launchInViewModel {
            val accountIdResult =
                webFingerBaseUrlToUserIdRepo.getUserId(userUriInsights.webFinger, role)
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
                }.map { mapAccountEntityEmoji(it) }
                .onSuccess { entity ->
                    _uiState.value = _uiState.value.copy(
                        joinedDatetime = dateFormat.format(formatDatetimeToDate(entity.createdAt)),
                        fieldList = entity.fields,
                    )
                }
        }
    }
}
