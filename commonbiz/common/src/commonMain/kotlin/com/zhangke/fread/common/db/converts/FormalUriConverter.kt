package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.fread.status.uri.FormalUri

class FormalUriConverter {

    @TypeConverter
    fun convertToString(uri: FormalUri): String {
        return uri.toString()
    }

    @TypeConverter
    fun convertToUri(uri: String): FormalUri {
        return FormalUri.from(uri)!!
    }
}
