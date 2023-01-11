package com.zhangke.utopia.blogprovider.db

import androidx.room.*
import com.zhangke.framework.room.ListStringConverter
import com.zhangke.framework.security.Md5
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.blogprovider.BlogFeeds
import com.zhangke.utopia.blogprovider.BlogFeedsShell
import com.zhangke.utopia.blogprovider.BlogSource

// FeedsSource 表示一个 Feeds 的源；
// BlogSource 表示单一 blog 源；
// 一个 FeedsSource 可能包含多个 BlogSource。

private const val DB_NAME = "FeedsSource"
private const val DB_VERSION = 1
private const val TABLE_FEEDS_SOURCE = "FeedsSources"
private const val TABLE_BLOG_SOURCE = "BlogSources"

@Entity(tableName = TABLE_FEEDS_SOURCE)
private data class FeedsEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val sourceList: List<String>
)

@Dao
private interface FeedsSourceDao {

    @Query("SELECT * FROM $TABLE_FEEDS_SOURCE")
    suspend fun queryAll(): List<FeedsEntry>

    @Query("SELECT * FROM $TABLE_FEEDS_SOURCE WHERE id = :id")
    suspend fun queryFeedsById(id: Int): FeedsEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FeedsEntry)

    @Delete
    suspend fun delete(entry: FeedsEntry)
}

@Entity(tableName = TABLE_BLOG_SOURCE)
private data class BlogSourceEntry(
    @PrimaryKey val id: String,
    val sourceServer: String,
    val protocol: String,
    val sourceName: String?,
    val sourceDescription: String?,
    val avatar: String?,
)

@Dao
private interface BlogSourceDao {

    @Query("SELECT * FROM $TABLE_BLOG_SOURCE")
    suspend fun queryAll(): List<BlogSourceEntry>

    @Query("SELECT * FROM $TABLE_BLOG_SOURCE WHERE id = :id")
    suspend fun queryById(id: String): BlogSourceEntry?

    @Query("SELECT * FROM $TABLE_BLOG_SOURCE WHERE id in (:idList)")
    suspend fun queryByIdList(idList: List<String>): List<BlogSourceEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: BlogSourceEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entryList: List<BlogSourceEntry>)

    @Delete
    suspend fun delete(entry: BlogSourceEntry)
}

@Database(
    entities = [FeedsEntry::class, BlogSourceEntry::class],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(ListStringConverter::class)
private abstract class BlogSourceDatabase : RoomDatabase() {

    abstract fun getFeedsSourceDao(): FeedsSourceDao

    abstract fun getBlogSourceDao(): BlogSourceDao

    companion object {

        val instance: BlogSourceDatabase by lazy { createInstance() }

        private fun createInstance(): BlogSourceDatabase {
            return Room.databaseBuilder(appContext, BlogSourceDatabase::class.java, DB_NAME)
                .build()
        }
    }
}

object BlogSourceRepo {

    private val feedsSourceDao: FeedsSourceDao
        get() = BlogSourceDatabase.instance.getFeedsSourceDao()

    private val blogSourceDao: BlogSourceDao
        get() = BlogSourceDatabase.instance.getBlogSourceDao()

    suspend fun insertFeeds(feeds: BlogFeeds) {
        val blogSourceEntryList = feeds.sourceList.map { it.toEntry() }
        blogSourceDao.insert(blogSourceEntryList)
        val feedsEntry = FeedsEntry(
            name = feeds.name,
            sourceList = blogSourceEntryList.map { it.id }
        )
        feedsSourceDao.insert(feedsEntry)
    }

    suspend fun queryAllFeedsShell(): List<BlogFeedsShell> {
        return feedsSourceDao.queryAll().map { it.toShell() }
    }

    suspend fun queryFeedsById(feedsId: Int): BlogFeeds? {
        val feedsEntry = feedsSourceDao.queryFeedsById(feedsId) ?: return null
        val sourceIdList = feedsEntry.sourceList
        return BlogFeeds(
            id = feedsEntry.id,
            name = feedsEntry.name,
            sourceList = blogSourceDao.queryByIdList(sourceIdList).map { it.toSource() }
        )
    }

    private fun FeedsEntry.toShell(): BlogFeedsShell {
        return BlogFeedsShell(
            id = id,
            name = name,
            sourceIdList = sourceList
        )
    }

    private fun BlogSource.toEntry(): BlogSourceEntry {
        return BlogSourceEntry(
            id = generateBlogSourceKey(this),
            sourceName = sourceName,
            sourceServer = sourceServer,
            sourceDescription = sourceDescription,
            protocol = protocol,
            avatar = avatar
        )
    }

    private fun BlogSourceEntry.toSource(): BlogSource {
        return BlogSource(
            sourceName = sourceName,
            sourceServer = sourceServer,
            sourceDescription = sourceDescription,
            protocol = protocol,
            avatar = avatar
        )
    }

    private fun generateBlogSourceKey(source: BlogSource): String {
        val serverMd5 = Md5.md5(source.sourceServer)
        return "${source.protocol}:$serverMd5"
    }
}