package com.zhangke.fread.common.status.usecase

import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.utils.DateTimeFormatter
import com.zhangke.fread.common.utils.DatetimeFormatConfig
import com.zhangke.fread.common.utils.defaultFormatConfig
import me.tatarka.inject.annotations.Inject

class FormatStatusDisplayTimeUseCase @Inject constructor(
    private val context: ApplicationContext,
) {

    operator fun invoke(
        datetime: Long,
        config: DatetimeFormatConfig = defaultFormatConfig(context),
    ): String {
        return DateTimeFormatter.format(context, datetime, config)
    }
}
