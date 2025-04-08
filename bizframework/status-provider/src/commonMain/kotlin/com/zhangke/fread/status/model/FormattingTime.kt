package com.zhangke.fread.status.model

import androidx.compose.runtime.Composable
import com.zhangke.framework.datetime.Instant
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.utils.DateTimeFormatter
import com.zhangke.fread.status.utils.defaultFormatConfig
import com.zhangke.fread.status.utils.defaultUiFormatConfig
import kotlinx.serialization.Serializable

@Serializable
data class FormattingTime(val time: Instant): PlatformSerializable {

    private var _formattedTime: String? = null

    @Composable
    fun formattedTime(): String {
        if (!_formattedTime.isNullOrEmpty()) return _formattedTime!!
        _formattedTime = DateTimeFormatter.format(time.epochMillis, defaultUiFormatConfig())
        return _formattedTime!!
    }

    suspend fun parse(): String {
        if (!_formattedTime.isNullOrEmpty()) return _formattedTime!!
        _formattedTime = DateTimeFormatter.format(time.epochMillis, defaultFormatConfig())
        return _formattedTime!!
    }
}
