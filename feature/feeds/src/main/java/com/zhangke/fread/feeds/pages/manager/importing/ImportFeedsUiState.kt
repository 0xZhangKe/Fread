package com.zhangke.fread.feeds.pages.manager.importing

import android.net.Uri
import com.zhangke.framework.collections.container
import com.zhangke.fread.status.uri.FormalUri

data class ImportFeedsUiState(
    val selectedFileUri: Uri?,
    val sourceList: List<ImportSourceGroup>,
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
        )
    }
}

val List<ImportSourceGroup>.importing: Boolean
    get() {
        return container { it.importing }
    }

sealed interface ImportingUiItem {

    data class Group(val group: ImportSourceGroup) : ImportingUiItem

    data class Source(val group: ImportSourceGroup, val source: ImportingSource) : ImportingUiItem
}

data class ImportSourceGroup(
    val title: String,
    val children: List<ImportingSource>,
) {

    val importing: Boolean
        get() {
            return children.container { it is ImportingSource.Importing }
        }
}

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
