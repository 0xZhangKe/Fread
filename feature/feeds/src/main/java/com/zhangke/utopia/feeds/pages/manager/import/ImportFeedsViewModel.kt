package com.zhangke.utopia.feeds.pages.manager.import

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.opml.OpmlOutline
import com.zhangke.framework.opml.OpmlParser
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.uri.FormalUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ImportFeedsViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val configRepo: ContentConfigRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ImportFeedsUiState(
            selectedFileUri = null,
            importing = false,
            parsedContent = emptyList(),
            outputInfoList = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onFileSelected(uri: Uri) {
        _uiState.value = _uiState.value.copy(selectedFileUri = uri)
    }

    fun onImportClick(context: Context) {
        _uiState.update { it.copy(importing = true) }
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            _uiState.update {
                it.copy(
                    outputInfoList = it.outputInfoList + ImportOutputLog(
                        log = throwable.message ?: "Unknown error",
                        type = ImportOutputLog.Type.ERROR,
                    ),
                    importing = false,
                )
            }
        }
        launchInViewModel(exceptionHandler) {
            appendOutputLog("Start parsing outlines...")
            val list = parseOpml(context, uiState.value.selectedFileUri!!)
            appendOutputLog("Parsed ${list.size} outlines.")
            list.forEach {
                parseOutlineToContent(it)
            }
            appendOutputLog("All outlines parsed.")
        }
    }

    private suspend fun parseOutlineToContent(outline: OpmlOutline) {
        appendOutputLog("Start parsing ${outline.title}...")
        val sourceUriList = mutableListOf<FormalUri>()
        outline.children
            .chunked(6)
            .forEach { list ->
                list.map { outline ->
                    viewModelScope.async {
                        appendOutputLog("Checkout ${outline.xmlUrl}...")
                        statusProvider.statusSourceResolver
                            .resolveRssSource(outline.xmlUrl)
                            .onSuccess {
                                sourceUriList += it.uri
                                appendOutputLog("Checkout ${outline.xmlUrl} success.")
                            }.onFailure {
                                appendOutputLog("Checkout ${outline.xmlUrl} failed: ${it.message}.")
                            }
                    }
                }.awaitAll()
            }
        val contentConfig = ContentConfig.MixedContent(
            id = 0,
            order = configRepo.generateNextOrder(),
            name = outline.title,
            sourceUriList = sourceUriList,
        )
        appendOutputLog("${outline.title} parsed successfully.")
        _uiState.update {
            it.copy(parsedContent = it.parsedContent + contentConfig)
        }
    }

    private fun appendOutputLog(
        log: String,
        type:
        ImportOutputLog.Type = ImportOutputLog.Type.NORMAL,
    ) {
        _uiState.update {
            it.copy(
                outputInfoList = it.outputInfoList + ImportOutputLog(
                    log = log,
                    type = type,
                )
            )
        }
    }

    private suspend fun parseOpml(
        context: Context,
        uri: Uri,
    ): List<OpmlOutline> = withContext(Dispatchers.IO) {
        val xmlDocument = context.contentResolver
            .openInputStream(uri)!!.use { inputStream ->
                String(inputStream.readBytes())
            }
        return@withContext OpmlParser.parse(xmlDocument)
    }
}
