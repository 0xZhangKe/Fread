package com.zhangke.fread.bluesky.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.fromJson
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import kotlinx.serialization.encodeToString

class BlueskyLoggedAccountConverter {

    @TypeConverter
    fun fromAccount(account: BlueskyLoggedAccount): String {
        return bskyJson.encodeToString(account)
    }

    @TypeConverter
    fun toAccount(text: String): BlueskyLoggedAccount {
        return bskyJson.fromJson(text)
    }
}
