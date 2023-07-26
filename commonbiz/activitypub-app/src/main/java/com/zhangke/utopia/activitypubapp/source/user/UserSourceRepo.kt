package com.zhangke.utopia.activitypubapp.source.user

import androidx.room.*
import com.zhangke.utopia.activitypubapp.db.ActivityPubDatabases
import com.zhangke.utopia.activitypubapp.utils.WebFinger
import javax.inject.Inject

private const val TABLE_NAME = "UserSources"

@Entity(tableName = TABLE_NAME)
data class UserSourceEntry(
    @PrimaryKey val webFinger: WebFinger,
    val userId: String,
    val nickName: String,
    val description: String,
    val thumbnail: String?,
)

@Dao
interface UserSourceDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE webFinger=:webFinger")
    suspend fun query(webFinger: WebFinger): UserSourceEntry?

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<UserSourceEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: UserSourceEntry)
}

class UserSourceRepo @Inject constructor() {

    private val databases = ActivityPubDatabases.instance

    private val dao: UserSourceDao get() = databases.getUserSourceDao()

    private val webFingerToSourceCache = mutableMapOf<WebFinger, UserSource>()

//    suspend fun query(webFinger: WebFinger): UserSource? {
//        val cached = webFingerToSourceCache[webFinger]
//        if (cached != null) return cached
//        return dao.query(webFinger)
//            ?.let { activityPubAccountAdapter.fromUserSourceEntity(it) }
//            ?.also { webFingerToSourceCache[webFinger] = it }
//    }

//    suspend fun save(userSource: UserSource) {
//        dao.insert(activityPubAccountAdapter.toSourceEntityEntry(userSource))
//    }
}