package com.zhangke.utopia.activitypubapp.source.user

import androidx.room.*
import com.zhangke.utopia.activitypubapp.db.ActivityPubDatabases
import com.zhangke.utopia.activitypubapp.utils.WebFinger

private const val TABLE_NAME = "UserSources"

@Entity(tableName = TABLE_NAME)
internal data class UserSourceEntry(
    @PrimaryKey val webFinger: WebFinger,
    val userId: String,
    val nickName: String,
    val description: String,
    val thumbnail: String?,
)

@Dao
internal interface UserSourceDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE webFinger=:webFinger")
    suspend fun query(webFinger: WebFinger): UserSourceEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: UserSourceEntry)
}

internal object UserSourceRepo {

    private val dao: UserSourceDao get() = ActivityPubDatabases.instance.getUserSourceDao()

    suspend fun query(webFinger: WebFinger): UserSource? {
        return dao.query(webFinger)?.toUserSource()
    }

    suspend fun save(userSource: UserSource) {
        dao.insert(userSource.toEntry())
    }

    private fun UserSourceEntry.toUserSource(): UserSource {
        return UserSource(
            nickName = nickName,
            webFinger = webFinger,
            description = description,
            thumbnail = thumbnail,
            userId = userId,
        )
    }

    private fun UserSource.toEntry(): UserSourceEntry {
        return UserSourceEntry(
            nickName = nickName,
            webFinger = webFinger,
            description = description,
            thumbnail = thumbnail,
            userId = userId,
        )
    }
}