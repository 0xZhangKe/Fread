package com.zhangke.utopia.feeds.pages.manager.importing

import android.net.Uri
import com.zhangke.utopia.status.model.ContentConfig

data class ImportFeedsUiState(
    val selectedFileUri: Uri?,
    val importing: Boolean,
    val parsedContent: List<ContentConfig>,
    val outputInfoList: List<ImportOutputLog>,
)

data class ImportOutputLog(
    val log: String,
    val type: Type = Type.NORMAL,
) {

    enum class Type {
        NORMAL, WARNING, ERROR,
    }
}
