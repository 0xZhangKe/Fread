package com.zhangke.fread.feeds.pages.manager.importing

import android.net.Uri
import com.zhangke.fread.status.model.ContentConfig

data class ImportFeedsUiState(
    val selectedFileUri: Uri?,
    val importType: ImportType,
    val parsedContent: List<ContentConfig>,
    val outputInfoList: List<ImportOutputLog>,
) {

    companion object {

        val default = ImportFeedsUiState(
            selectedFileUri = null,
            importType = ImportType.IDLE,
            parsedContent = emptyList(),
            outputInfoList = emptyList(),
        )
    }
}

enum class ImportType {

    IDLE,
    IMPORTING,
    SUCCESS,
    FAILED,
    ;
}

data class ImportOutputLog(
    val log: String,
    val type: Type = Type.NORMAL,
) {

    enum class Type {
        NORMAL, WARNING, ERROR,
    }
}
