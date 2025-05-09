package com.zhangke.fread.feeds.pages.manager.importing

import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.status.uri.FormalUri

data class ImportFeedsUiState(
    val selectedFileUri: PlatformUri?,
    val sourceList: List<ImportSourceGroup>,
    val errorMessage: String? = null,
) {

    val importingUiItems: List<ImportingUiItem> by lazy {
        createUiItems()
    }

    private fun createUiItems(): List<ImportingUiItem> {
        return sourceList.flatMap { group ->
            listOf(ImportingUiItem.Group(group)) + group.children.map { ImportingUiItem.Source(group, it) }
        }
    }

    companion object {

        val default = ImportFeedsUiState(
            selectedFileUri = null,
            sourceList = emptyList(),
            errorMessage = null,
        )
    }
}

sealed interface ImportingUiItem {

    data class Group(val group: ImportSourceGroup) : ImportingUiItem

    data class Source(val group: ImportSourceGroup, val source: ImportingSource) : ImportingUiItem
}

data class ImportSourceGroup(
    val title: String,
    val children: List<ImportingSource>,
)

sealed interface ImportingSource {

    val title: String
    val url: String

    data class Importing(
        override val title: String,
        override val url: String,
    ) : ImportingSource

    data class Success(
        override val title: String,
        override val url: String,
        val formalUri: FormalUri,
    ) : ImportingSource

    data class Failure(
        override val title: String,
        override val url: String,
        val errorMessage: String,
    ) : ImportingSource

    data class Pending(
        override val title: String,
        override val url: String
    ) : ImportingSource
}
