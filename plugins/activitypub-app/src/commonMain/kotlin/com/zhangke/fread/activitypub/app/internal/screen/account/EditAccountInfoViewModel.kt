package com.zhangke.fread.activitypub.app.internal.screen.account

import androidx.lifecycle.ViewModel
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.UpdateFieldRequestEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class EditAccountInfoViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val platformUriHelper: PlatformUriHelper,
    @Assisted private val baseUrl: FormalBaseUrl,
    @Assisted private val accountUri: FormalUri,
) : ViewModel() {

    companion object {

        const val FIELD_MAX_COUNT = 4
    }

    fun interface Factory : ViewModelFactory {
        fun create(baseUrl: FormalBaseUrl, uri: FormalUri): EditAccountInfoViewModel
    }

    private val _uiState = MutableStateFlow(
        EditAccountUiState(
            name = "",
            header = "",
            avatar = "",
            description = "",
            fieldList = emptyList(),
            fieldAddable = false,
            requesting = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow: SharedFlow<TextString> = _snackBarMessageFlow.asSharedFlow()

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow = _finishPageFlow.asSharedFlow()

    private var originalAccountInfo: ActivityPubAccountEntity? = null

    private val locator = PlatformLocator(accountUri = accountUri, baseUrl = baseUrl)

    init {
        launchInViewModel {
            clientManager.getClient(locator)
                .accountRepo
                .getCredentialAccount()
                .onSuccess {
                    updateUiStateByEntity(it)
                    originalAccountInfo = it
                }.onFailure { e ->
                    e.message?.let { textOf(it) }?.let { _snackBarMessageFlow.emit(it) }
                }
        }
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

    fun onFieldDelete(idForUi: Int) {
        _uiState.update { currentState ->
            val newFieldList = currentState.fieldList
                .filter { it.idForUi != idForUi }
            currentState.copy(
                fieldList = newFieldList,
                fieldAddable = newFieldList.size < FIELD_MAX_COUNT,
            )
        }
    }

    fun onFieldAddClick() {
        _uiState.update { currentState ->
            val newFieldList = currentState.fieldList.toMutableList()
            newFieldList.add(
                EditAccountFieldUiState(
                    idForUi = newFieldList.size,
                    name = "",
                    value = "",
                )
            )
            currentState.copy(
                fieldList = newFieldList,
                fieldAddable = newFieldList.size < FIELD_MAX_COUNT,
            )
        }
    }

    fun onAvatarSelected(uri: PlatformUri) {
        _uiState.update { it.copy(avatar = uri.toString()) }
    }

    fun onHeaderSelected(uri: PlatformUri) {
        _uiState.update { it.copy(header = uri.toString()) }
    }

    fun onEditClick() = launchInViewModel {
        val originalAccountInfo = originalAccountInfo ?: return@launchInViewModel
        val currentUiState = _uiState.value
        if (currentUiState.name.isBlank()) {
            _snackBarMessageFlow.emit(textOf(LocalizedString.editProfileNameEmpty))
            return@launchInViewModel
        }
        val newName = currentUiState.name.takeIf { it != originalAccountInfo.nameOfUiState }
        val newNote = currentUiState.description.takeIf { it != originalAccountInfo.noteOfUiState }
        val newAvatar = currentUiState.avatar
            .takeIf { it != originalAccountInfo.avatar }
            ?.toPlatformUri()
            ?.let { platformUriHelper.read(it) }
        val newHeader = currentUiState.header
            .takeIf { it != originalAccountInfo.header }
            ?.toPlatformUri()
            ?.let { platformUriHelper.read(it) }
        val newFieldList =
            currentUiState.fieldList.takeIf { !it.compare(originalAccountInfo.fieldOfUiState) }
        if (newName == null &&
            newNote == null &&
            newAvatar == null &&
            newHeader == null &&
            newFieldList == null
        ) {
            return@launchInViewModel
        }
        _uiState.update { it.copy(requesting = true) }
        clientManager.getClient(locator)
            .accountRepo
            .updateCredentials(
                name = newName,
                note = newNote,
                avatarFileName = newAvatar?.fileName,
                avatarByteArray = newAvatar?.readBytes(),
                headerFileName = newHeader?.fileName,
                headerByteArray = newHeader?.readBytes(),
                fieldList = newFieldList?.map { it.toUpdateFieldRequestEntity() },
            ).onSuccess {
                updateUiStateByEntity(it)
                _finishPageFlow.emit(Unit)
            }.onFailure { e ->
                e.message?.let { textOf(it) }?.let { _snackBarMessageFlow.emit(it) }
                _uiState.update { it.copy(requesting = false) }
            }
    }

    private fun updateUiStateByEntity(entity: ActivityPubAccountEntity) {
        originalAccountInfo = entity
        _uiState.update { currentState ->
            val fieldList = entity.fieldOfUiState
            currentState.copy(
                name = entity.nameOfUiState,
                header = entity.headerOfUiState,
                avatar = entity.avatarOfUiState,
                description = entity.noteOfUiState,
                fieldList = fieldList,
                fieldAddable = fieldList.size < FIELD_MAX_COUNT,
                requesting = false,
            )
        }
    }

    private val ActivityPubAccountEntity.nameOfUiState: String get() = displayName

    private val ActivityPubAccountEntity.headerOfUiState: String get() = header

    private val ActivityPubAccountEntity.avatarOfUiState: String get() = avatar

    private val ActivityPubAccountEntity.noteOfUiState: String get() = source?.note.orEmpty()

    private val ActivityPubAccountEntity.fieldOfUiState: List<EditAccountFieldUiState>
        get() = source?.fields?.mapIndexed { index, field ->
            EditAccountFieldUiState(
                idForUi = index,
                name = field.name,
                value = field.value
            )
        } ?: emptyList()

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
