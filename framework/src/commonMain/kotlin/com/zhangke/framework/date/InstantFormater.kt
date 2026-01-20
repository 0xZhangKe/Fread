@file:OptIn(ExperimentalTime::class)

package com.zhangke.framework.date

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

expect class InstantFormater() {

    fun formatToMediumDate(instant: Instant): String

    fun formatToMediumDateWithoutTime(instant: Instant): String
}
