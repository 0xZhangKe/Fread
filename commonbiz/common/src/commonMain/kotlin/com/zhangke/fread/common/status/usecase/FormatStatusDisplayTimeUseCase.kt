package com.zhangke.fread.common.status.usecase

import com.zhangke.fread.status.utils.DateTimeFormatter
import com.zhangke.fread.status.utils.DatetimeFormatConfig
import com.zhangke.fread.status.utils.defaultFormatConfig

class FormatStatusDisplayTimeUseCase (
) {

    suspend operator fun invoke(
        datetime: Long,
    ): String {
        return invoke(
            datetime,
            config = defaultFormatConfig(),
        )
    }

    operator fun invoke(
        datetime: Long,
        config: DatetimeFormatConfig,
    ): String {
        return DateTimeFormatter.format(datetime, config)
    }
}