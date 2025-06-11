package com.zhangke.fread.common.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zhangke.fread.status.model.FreadContent

private const val DB_VERSION = 1
private const val TABLE_NAME = "fread_content"

@Entity(tableName = TABLE_NAME)
data class FreadContentEntity(
    @PrimaryKey val id: String,
    val content: FreadContent,
)

class FreadContentDatabase {
}