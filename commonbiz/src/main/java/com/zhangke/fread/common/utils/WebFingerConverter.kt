package com.zhangke.fread.common.utils

import androidx.room.TypeConverter
import com.zhangke.framework.utils.WebFinger

class WebFingerConverter {

    @TypeConverter
    fun fromWebFinger(webFinger: WebFinger): String {
        return webFinger.toString()
    }

    @TypeConverter
    fun toWebFinger(text: String): WebFinger {
        return WebFinger.create(text)!!
    }
}