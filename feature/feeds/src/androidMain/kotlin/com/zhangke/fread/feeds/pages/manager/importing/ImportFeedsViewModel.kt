package com.zhangke.fread.feeds.pages.manager.importing

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.collections.container
import com.zhangke.framework.collections.remove
import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.opml.OpmlOutline
import com.zhangke.framework.opml.OpmlParser
import com.zhangke.fread.analytics.report
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.ContentConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

class ImportFeedsViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val configRepo: ContentConfigRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportFeedsUiState.default)
    val uiState = _uiState.asStateFlow()

    private val _saveSuccessFlow = MutableSharedFlow<Unit>()
    val saveSuccessFlow = _saveSuccessFlow.asSharedFlow()

    private var importingJob: Job? = null

    fun onFileSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            selectedFileUri = uri,
            sourceList = emptyList(),
        )
    }

    fun onImportClick(context: Context) {
        if (importingJob?.isActive == true) return
        if (uiState.value.sourceList.isNotEmpty()) return
        _uiState.update { it.copy(errorMessage = null) }
        importingJob = viewModelScope.launch {
            val sourceGroup = parseOpmlToGroup(context, uiState.value.selectedFileUri!!)
            _uiState.update { it.copy(sourceList = sourceGroup) }
            sourceGroup.forEach {
                importGroup(it)
            }
        }
    }

    private suspend fun importGroup(group: ImportSourceGroup) {
        if (checkGroupDeleted(group)) return
        supervisorScope {
            group.children.map { source ->
                async {
                    importSource(group, source)
                }
            }.awaitAll()
        }
    }

    private suspend fun importSource(group: ImportSourceGroup, source: ImportingSource) {
        if (checkSourceDeleted(group, source)) return
        if (source !is ImportingSource.Pending && source !is ImportingSource.Failure) return
        updateSourceUiState(group, ImportingSource.Importing(source.title, source.url))
        statusProvider.statusSourceResolver.resolveRssSource(source.url)
            .onSuccess {
                updateSourceUiState(
                    group,
                    ImportingSource.Success(source.title, source.url, it.uri)
                )
            }.onFailure {
                updateSourceUiState(
                    group,
                    ImportingSource.Failure(source.title, source.url, it.message ?: "Unknown error")
                )
            }
    }

    private fun updateSourceUiState(group: ImportSourceGroup, source: ImportingSource) {
        _uiState.update { state ->
            state.copy(
                sourceList = state.sourceList.map { item ->
                    if (item.title == group.title) {
                        item.copy(
                            children = item.children.map { child ->
                                if (child.url == source.url) {
                                    source
                                } else {
                                    child
                                }
                            })
                    } else {
                        item
                    }
                }
            )
        }
    }

    fun onGroupDelete(group: ImportSourceGroup) {
        _uiState.update { state ->
            state.copy(
                sourceList = state.sourceList.remove { it == group }
            )
        }
    }

    fun onSourceDelete(group: ImportSourceGroup, source: ImportingSource) {
        _uiState.update { state ->
            state.copy(
                sourceList = state.sourceList.map { item ->
                    if (item == group) {
                        item.copy(children = item.children.remove { it == source })
                    } else {
                        item
                    }
                }
            )
        }
    }

    fun onSaveClick() {
        if (importingJob?.isActive == true) return
        viewModelScope.launch {
            val mixedContentList = _uiState.value
                .sourceList
                .mapNotNull { it.toMixedContent() }
            if (mixedContentList.isEmpty()) return@launch
            configRepo.insert(mixedContentList)
            _saveSuccessFlow.emit(Unit)
        }
    }

    fun retryImportClick(group: ImportSourceGroup, source: ImportingSource) {
        viewModelScope.launch {
            importSource(group, source)
        }
    }

    private fun checkGroupDeleted(group: ImportSourceGroup): Boolean {
        val currentList = _uiState.value.sourceList
        return !currentList.container { it.title == group.title }
    }

    private fun checkSourceDeleted(group: ImportSourceGroup, source: ImportingSource): Boolean {
        val currentList = _uiState.value.sourceList
        val existGroup = currentList.firstOrNull { it.title == group.title } ?: return true
        return !existGroup.children.container { it.url == source.url }
    }

    private suspend fun ImportSourceGroup.toMixedContent(): ContentConfig.MixedContent? {
        val successChildren = this.children.filterIsInstance<ImportingSource.Success>()
        if (successChildren.isEmpty()) return null
        return ContentConfig.MixedContent(
            id = 0,
            order = configRepo.generateNextOrder(),
            name = this.title,
            sourceUriList = successChildren.map { it.formalUri },
        )
    }

    private suspend fun parseOpmlToGroup(context: Context, uri: Uri): List<ImportSourceGroup> {
        return parseOpml(context, uri).groupBy { it.title.ifEmpty { "Unknown" } }
            .map { entry ->
                ImportSourceGroup(
                    title = entry.key,
                    children = entry.value.flatMap { it.allChildren() }.map { it.toSource() }
                )
            }
    }

    private fun OpmlOutline.toSource(): ImportingSource {
        return ImportingSource.Pending(
            title = title.ifEmpty { convertSimpleNameFromUrl(xmlUrl) },
            url = xmlUrl
        )
    }

    private fun convertSimpleNameFromUrl(url: String): String {
        if (url.isBlank()) return "Unknown"
        return SimpleUri.parse(url)?.host ?: "Unknown"
    }

    private fun OpmlOutline.allChildren(): List<OpmlOutline> {
        val list = mutableListOf<OpmlOutline>()
        if (xmlUrl.isNotBlank()) {
            list.add(this)
        }
        children.forEach {
            list.addAll(it.allChildren())
        }
        return list
    }

    private suspend fun parseOpml(
        context: Context,
        uri: Uri,
    ): List<OpmlOutline> = withContext(Dispatchers.IO) {
        var xmlDocument = ""
        return@withContext try {
            xmlDocument = context.contentResolver
                .openInputStream(uri)!!.use { inputStream ->
                    String(inputStream.readBytes())
                }
            OpmlParser.parse(xmlDocument)
        } catch (e: Throwable) {
            _uiState.update { it.copy(errorMessage = e.message) }
            report("OPML_IMPORT_ERROR") {
                putString("errorMessage", e.message)
                putString("trace", e.stackTraceToString())
                putString("document", xmlDocument)
            }
            emptyList<OpmlOutline>()
        }
    }
}
