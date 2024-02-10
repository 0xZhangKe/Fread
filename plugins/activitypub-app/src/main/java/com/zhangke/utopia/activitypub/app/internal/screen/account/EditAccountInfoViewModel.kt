package com.zhangke.utopia.activitypub.app.internal.screen.account

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.UpdateFieldRequestEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
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
import kotlinx.coroutines.flow.update

@HiltViewModel
class EditAccountInfoViewModel @AssistedInject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo,
    private val clientManager: ActivityPubClientManager,
    private val userUriTransformer: UserUriTransformer,
    @Assisted private val accountUri: FormalUri,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(uri: FormalUri): EditAccountInfoViewModel
    }

    private val _uiState = MutableStateFlow(
        EditAccountUiState(
            name = "",
            banner = "",
            avatar = "",
            description = "",
            fieldList = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow: SharedFlow<TextString> = _snackBarMessageFlow.asSharedFlow()

    private var originalAccountInfo: EditAccountUiState? = null

    private val baseUrl: FormalBaseUrl get() = userUriTransformer.parse(accountUri)!!.baseUrl

    init {
        launchInViewModel {
            clientManager.getClient(baseUrl).accountRepo
                .getCredentialAccount()
                .onSuccess {
                    _uiState.value = it.toUiState()
                    originalAccountInfo = it.toUiState()
                }.onFailure { e ->
                    e.message?.let { textOf(it) }?.let { _snackBarMessageFlow.emit(it) }
                }
        }
    }

    private fun ActivityPubAccountEntity.toUiState(): EditAccountUiState {
        return EditAccountUiState(
            name = displayName,
            banner = header,
            avatar = avatar,
            description = source.note,
            fieldList = source.fields.mapIndexed { index, field ->
                EditAccountFieldUiState(
                    idForUi = index,
                    name = field.name,
                    value = field.value
                )
            }
        )
    }

    fun onUserNameInput(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onUserDescriptionInput(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onFieldInput(idForUi: Int, name: String, value: String) {
        _uiState.update { currentState ->
            val fieldList = currentState.fieldList
            currentState.copy(
                fieldList = fieldList.map {
                    if (it.idForUi == idForUi) {
                        it.copy(name = name, value = value)
                    } else {
                        it
                    }
                }
            )
        }
    }

    fun onFiledDelete(idForUi: Int) {
        _uiState.update { currentState ->
            val fieldList = currentState.fieldList
            currentState.copy(
                fieldList = fieldList.filter { it.idForUi != idForUi }
            )
        }
    }

    fun onEditClick() = launchInViewModel {
        val originalAccountInfo = originalAccountInfo ?: return@launchInViewModel
        val currentUiState = _uiState.value
        if (currentUiState.name.isBlank()) {
            _snackBarMessageFlow.emit(textOf(R.string.activity_pub_edit_account_info_name_empty))
            return@launchInViewModel
        }
        val newName = currentUiState.name.takeIf { it != originalAccountInfo.name }
        val newNote = currentUiState.description.takeIf { it != originalAccountInfo.description }
        val newAvatar = currentUiState.avatar.takeIf { it != originalAccountInfo.avatar }
        val newBanner = currentUiState.banner.takeIf { it != originalAccountInfo.banner }
        val newFieldList = currentUiState.fieldList.takeIf { !it.compare(originalAccountInfo.fieldList) }
        clientManager.getClient(baseUrl).accountRepo
            .updateCredentials(
                name = newName,
                note = newNote,
                fieldList = newFieldList?.map { it.toUpdateFieldRequestEntity() },
            )
    }

    /**
     * Compare theos two list is same or not
     */
    private fun List<EditAccountFieldUiState>.compare(
        original: List<EditAccountFieldUiState>
    ): Boolean {
        if (size != original.size) {
            return false
        }
        this.forEach { item ->
            val originalState = original.firstOrNull { it.idForUi == item.idForUi }
            if (originalState == null || originalState.name != item.name || originalState.value != item.value) {
                return false
            }
        }
        return true
    }

    private fun EditAccountFieldUiState.toUpdateFieldRequestEntity() = UpdateFieldRequestEntity(
        name = name,
        value = value
    )
}
