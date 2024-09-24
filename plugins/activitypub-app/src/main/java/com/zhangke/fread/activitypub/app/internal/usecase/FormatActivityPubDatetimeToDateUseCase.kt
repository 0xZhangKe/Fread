package com.zhangke.fread.activitypub.app.internal.usecase

import com.zhangke.framework.date.DateParser
import com.zhangke.fread.common.ext.toJavaDate
import java.util.Date
import me.tatarka.inject.annotations.Inject

class FormatActivityPubDatetimeToDateUseCase @Inject constructor() {

    operator fun invoke(datetime: String): Date {
        return DateParser.parseAll(datetime)?.toJavaDate() ?: Date()
    }
}
