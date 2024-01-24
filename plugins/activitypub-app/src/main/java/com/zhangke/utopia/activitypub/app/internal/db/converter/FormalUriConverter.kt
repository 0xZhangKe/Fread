package com.zhangke.utopia.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.utopia.status.uri.FormalUri

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
