package com.zhangke.utopia.activitypub.app.internal.instance

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zhangke.framework.network.FormalBaseUrl

private const val DB_VERSION = 1
private const val DB_NAME = "mastodon_instances.db"
private const val TABLE_NAME = "instances"

@Entity(tableName = TABLE_NAME)
data class MastodonInstanceEntity(
    @PrimaryKey val baseUrl: FormalBaseUrl,

)

class MastodonInstanceDatabase {
}