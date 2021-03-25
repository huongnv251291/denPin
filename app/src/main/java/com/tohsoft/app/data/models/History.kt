package com.tohsoft.app.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "History", indices = [Index(value = ["title"], unique = true)])
data class History constructor(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
        val id: Int,
        val uuid: String,
        var title: String,
        val created: Long
)
