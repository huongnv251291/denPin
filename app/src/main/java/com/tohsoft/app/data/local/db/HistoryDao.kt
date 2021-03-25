package com.tohsoft.app.data.local.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.tohsoft.app.data.models.History

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: History)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(histories: List<History>)

    @Update
    suspend fun update(history: History)

    @Delete
    suspend fun delete(history: History)

    @Query("DELETE FROM History")
    suspend fun deleteAll()

    @Query("SELECT * FROM History ORDER BY created")
    fun getHistories(): LiveData<List<History>>

    @Query("SELECT * FROM History ORDER BY created")
    fun getHistoriesPaging(): DataSource.Factory<Int, History>

    @Query("SELECT * FROM History WHERE title LIKE '%' || :title || '%'")
    fun searchHistoryWithTitle(title: String): DataSource.Factory<Int, History>
}