package com.zhangke.framework.date

import kotlinx.datetime.Instant

expect class InstantFormater() {

    fun formatToMediumDate(instant: Instant): String
}
