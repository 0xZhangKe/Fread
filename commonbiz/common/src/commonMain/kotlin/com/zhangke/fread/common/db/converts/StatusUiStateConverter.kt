package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.fread.status.model.StatusUiState
import kotlinx.serialization.json.Json

class StatusUiStateConverter {

    @TypeConverter
    fun convertToText(status: StatusUiState): String {
        return Json.encodeToString(StatusUiState.serializer(), status)
    }

    @TypeConverter
    fun convertToStatus(text: String): StatusUiState {
        return Json.decodeFromString(StatusUiState.serializer(), text)
    }
}
