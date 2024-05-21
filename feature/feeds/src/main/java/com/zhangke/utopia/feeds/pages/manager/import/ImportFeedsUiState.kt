package com.zhangke.utopia.feeds.pages.manager.import

import android.net.Uri
import com.zhangke.utopia.status.model.ContentConfig
import java.net.URLDecoder

data class ImportFeedsUiState(
    val selectedFileUri: Uri?,
    val importing: Boolean,
    val parsedContent: List<ContentConfig>,
    val outputInfoList: List<ImportOutputLog>,
) {

    val prettyFileUri: String?
        get() {
            selectedFileUri ?: return null
            return try {
                val path = URLDecoder.decode(selectedFileUri.path, "UTF-8")
                path.split("/").lastOrNull() ?: path
            } catch (e: Throwable) {
                null
            }
        }
}

data class ImportOutputLog(
    val log: String,
    val type: Type = Type.NORMAL,
) {

    enum class Type {
        NORMAL, WARNING, ERROR,
    }
}
