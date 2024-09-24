package com.zhangke.fread.common.status.usecase

import com.zhangke.fread.common.utils.DateTimeFormatter
import com.zhangke.fread.common.utils.DatetimeFormatConfig
import com.zhangke.fread.common.utils.defaultFormatConfig
import me.tatarka.inject.annotations.Inject

class FormatStatusDisplayTimeUseCase @Inject constructor(
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
