package com.zhangke.utopia.common.feeds.repo.content

import androidx.room.Entity
import androidx.room.PrimaryKey

private const val DB_VERSION = 1
private const val TABLE_NAME = "feeds_content"

@Entity(tableName = TABLE_NAME)
data class FeedsContent(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val sourceUri: String,
    val url: String?,

)

class FeedsContentDataBase {
}