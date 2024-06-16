package com.zhangke.fread.common.status.usecase

import android.content.Context
import com.zhangke.fread.common.utils.DateTimeFormatter
import com.zhangke.fread.common.utils.DatetimeFormatConfig
import com.zhangke.fread.common.utils.defaultFormatConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FormatStatusDisplayTimeUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    operator fun invoke(
        datetime: Long,
        config: DatetimeFormatConfig = defaultFormatConfig(context),
    ): String {
        return DateTimeFormatter.format(context, datetime, config)
    }
}
